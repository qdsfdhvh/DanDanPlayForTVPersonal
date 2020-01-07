/*
 * Copyright (C) 2016-2018 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of LibreTorrent.
 *
 * LibreTorrent is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LibreTorrent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LibreTorrent.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.seiko.module.torrent.ui.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.TypedValue
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.seiko.module.torrent.R
import com.seiko.module.torrent.constants.*
import com.seiko.module.torrent.extensions.getClipboard
import com.seiko.module.torrent.extensions.isHash
import com.seiko.module.torrent.extensions.isMagnet
import com.seiko.module.torrent.model.AddTorrentParams
import com.seiko.module.torrent.model.sort.BaseSorting
import com.seiko.module.torrent.model.sort.TorrentSorting
import com.seiko.module.torrent.model.sort.TorrentSortingComparator
import com.seiko.module.torrent.service.TorrentTaskService
import com.seiko.module.torrent.ui.adapters.ToolbarSpinnerAdapter
import com.seiko.module.torrent.ui.adapters.TorrentListAdapter
import com.seiko.module.torrent.ui.adapters.TorrentListItem
import com.seiko.module.torrent.ui.dialogs.BaseAlertDialog
import com.seiko.module.torrent.ui.widgets.RecyclerViewDividerDecoration
import com.seiko.torrent.constants.TorrentStateCode
import kotlinx.android.synthetic.main.torrent_fragment_main_left.*
import kotlinx.android.synthetic.main.torrent_toolbar_spinner.view.*
import java.util.*

/*
 * The list of torrents.
 */

class MainLeftFragment : BaseFragment(), BaseAlertDialog.OnClickListener,
    BaseAlertDialog.OnDialogShowListener {

    /* Save state scrolling */
    private var torrentsListState: Parcelable? = null
    private var actionMode: ActionMode? = null
    private val actionModeCallback = ActionModeCallback()
    private var inActionMode = false
    private var selectedTorrents: ArrayList<String> = ArrayList()
    private var addTorrentMenu = false

    private lateinit var mActivity: AppCompatActivity
    private lateinit var spinnerAdapter: ToolbarSpinnerAdapter
    private lateinit var adapter: TorrentListAdapter
    private lateinit var layoutManager: LinearLayoutManager

    /* Prevents re-adding the torrent, obtained through implicit intent */
    private var prevImplIntent: Intent? = null

    /*
     * A RecyclerView by default creates another copy of the ViewHolder in order to
     * fade the views into each other. This causes the problem because the old ViewHolder gets
     * the payload but then the new one doesn't. So needs to explicitly tell it to reuse the old one.
     */
    private val animator: RecyclerView.ItemAnimator
        get() {
            return object : DefaultItemAnimator() {
                override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
                    return true
                }
            }
        }

    /*
     * Returns a list of torrents sorting categories for spinner.
     */
    private val spinnerList: List<String>
        get() {
            val categories = ArrayList<String>()
            categories.add(getString(R.string.torrent_spinner_all_torrents))
            categories.add(getString(R.string.torrent_spinner_downloading_torrents))
            categories.add(getString(R.string.torrent_spinner_downloaded_torrents))
            categories.add(getString(R.string.torrent_spinner_downloading_metadata_torrents))
            return categories
        }

    private var torrentListListener: TorrentListAdapter.ViewHolder.ClickListener =
        object : TorrentListAdapter.ViewHolder.ClickListener {
            override fun onItemClicked(position: Int, item: TorrentListItem) {
                if (actionMode == null) {
                    /* Mark this torrent as open in the list */
                    adapter.markAsOpen(item)
//                    showDetailTorrent(item.torrentId)
                } else {
//                    onItemSelected(item.torrentId, position)
                }
            }

            override fun onItemLongClicked(position: Int, item: TorrentListItem): Boolean {
                if (actionMode == null)
                    actionMode = activity!!.startActionMode(actionModeCallback)
//                onItemSelected(item.torrentId, position)

                return true
            }

            override fun onPauseButtonClicked(position: Int, item: TorrentListItem) {
                //            TorrentHelper.pauseResumeTorrent(item.torrentId);
            }
        }

    override fun getLayoutId(): Int {
        return R.layout.torrent_fragment_main_left
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AppCompatActivity) {
            mActivity = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setTitle(R.string.app_name)

        val a = mActivity.obtainStyledAttributes(TypedValue().data, intArrayOf(R.attr.torrent_divider))
        torrent_list.itemAnimator = animator
        torrent_list.addItemDecoration(RecyclerViewDividerDecoration(a.getDrawable(0)))
        torrent_list.setEmptyView(view.findViewById(R.id.empty_view_torrent_list))
        a.recycle()

        adapter = TorrentListAdapter(mActivity,
            R.layout.torrent_item_torrent_list,
            torrentListListener,
            TorrentSortingComparator( TorrentSorting(
                TorrentSorting.SortingColumns.name,
                BaseSorting.Direction.ASC)
            )
        )
        torrent_list.adapter = adapter

        val spinnerContainer = LayoutInflater.from(view.context).inflate(R.layout.torrent_toolbar_spinner, toolbar, false)
        toolbar.addView(spinnerContainer, ActionBar.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT))

        spinnerAdapter = ToolbarSpinnerAdapter(activity)
        spinnerAdapter.addItems(spinnerList)

        spinnerContainer.toolbar_spinner.adapter = spinnerAdapter
        spinnerContainer.toolbar_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                setTorrentListFilter(spinnerAdapter.getItem(position))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        setTorrentListFilter(spinnerContainer.toolbar_spinner.selectedItem.toString())

        mActivity.setSupportActionBar(toolbar)
        setHasOptionsMenu(true)

        add_torrent_button.setClosedOnTouchOutside(true)

        open_file_button.setOnClickListener {
            add_torrent_button.close(true)
//            torrentFileChooserDialog()
        }

        add_link_button.setOnClickListener {
            add_torrent_button.close(true)
            addLinkDialog()
        }

        create_torrent_button.setOnClickListener {
            add_torrent_button.close(true)
//            createTorrentDialog()
        }

        if (savedInstanceState != null) {
            prevImplIntent = savedInstanceState.getParcelable(TAG_PREV_IMPL_INTENT)
        }

        layoutManager = LinearLayoutManager(mActivity)
        torrent_list.layoutManager = layoutManager


        /* Show add torrent menu (called from service) after window is displayed */
        requireActivity().window.findViewById<View>(android.R.id.content).post {
            if (addTorrentMenu) {
                /* Hide notification bar */
                mActivity.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
                addTorrentMenu = false

                val v = mActivity.window.findViewById<View>(android.R.id.content)
                registerForContextMenu(v)
                mActivity.openContextMenu(v)
                unregisterForContextMenu(v)
            }
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(TAG_SELECTED_TORRENTS)) {
                selectedTorrents = savedInstanceState.getStringArrayList(TAG_SELECTED_TORRENTS)!!
            }
            if (savedInstanceState.getBoolean(TAG_IN_ACTION_MODE, false)) {
                actionMode = activity!!.startActionMode(actionModeCallback)
                adapter.selectedItems = savedInstanceState.getIntegerArrayList(TAG_SELECTABLE_ADAPTER)!!
                actionMode!!.title = adapter.selectedItemCount.toString()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (torrentsListState != null) {
            layoutManager.onRestoreInstanceState(torrentsListState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(TAG_PREV_IMPL_INTENT, prevImplIntent)
        outState.putIntegerArrayList(TAG_SELECTABLE_ADAPTER, adapter.selectedItems)
        outState.putBoolean(TAG_IN_ACTION_MODE, inActionMode)
        outState.putStringArrayList(TAG_SELECTED_TORRENTS, selectedTorrents)
        torrentsListState = layoutManager.onSaveInstanceState()
        outState.putParcelable(TAG_TORRENTS_LIST_STATE, torrentsListState)

        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            torrentsListState = savedInstanceState.getParcelable(TAG_TORRENTS_LIST_STATE)
        }
    }

    private inner class ActionModeCallback : ActionMode.Callback {
        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            inActionMode = true
            mode.menuInflater.inflate(R.menu.torrent_main_action_mode, menu)
            return true
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            adapter.clearSelection()
            actionMode = null
            inActionMode = false
        }
    }

    private fun addLinkDialog() {
        val fm = fragmentManager
        if (fm != null && fm.findFragmentByTag(TAG_ADD_LINK_DIALOG) == null) {
            val addLinkDialog = BaseAlertDialog.newInstance(
                getString(R.string.torrent_dialog_add_link_title), null,
                R.layout.torrent_dialog_text_input,
                getString(R.string.ok),
                getString(R.string.cancel), null,
                this
            )
            addLinkDialog.show(fm, TAG_ADD_LINK_DIALOG)
        }
    }

    private fun checkEditTextField(s: String?, layout: TextInputLayout?): Boolean {
        if (s == null || layout == null)
            return false

        if (TextUtils.isEmpty(s)) {
            layout.isErrorEnabled = true
            layout.error = getString(R.string.torrent_error_empty_link)
            layout.requestFocus()

            return false
        }

        layout.isErrorEnabled = false
        layout.error = null

        return true
    }

    private fun setTorrentListFilter(filter: String?) {
        if (filter == null) {
            return
        }

        when (filter) {
            getString(R.string.torrent_spinner_downloading_torrents) ->
                adapter.setDisplayFilter(TorrentListAdapter.DisplayFilter(TorrentStateCode.DOWNLOADING))
            getString(R.string.torrent_spinner_downloaded_torrents) ->
                adapter.setDisplayFilter(TorrentListAdapter.DisplayFilter(TorrentStateCode.SEEDING))
            getString(R.string.torrent_spinner_downloading_metadata_torrents) ->
                adapter.setDisplayFilter(TorrentListAdapter.DisplayFilter(TorrentStateCode.DOWNLOADING_METADATA))
            else ->
                adapter.setDisplayFilter(TorrentListAdapter.DisplayFilter())
        }
    }

//    private fun onItemSelected(id: String, position: Int) {
//        toggleSelection(position)
//
//        if (selectedTorrents.contains(id))
//            selectedTorrents.remove(id)
//        else
//            selectedTorrents.add(id)
//    }

//    private fun toggleSelection(position: Int) {
//        adapter.toggleSelection(position)
//        val count = adapter.selectedItemCount
//
//        if (actionMode != null) {
//            if (count == 0) {
//                actionMode!!.finish()
//            } else {
//                actionMode!!.title = count.toString()
//                actionMode!!.invalidate()
//            }
//        }
//    }


    override fun onShow(dialog: AlertDialog?) {
        if (dialog != null) {
            val fm = fragmentManager ?: return

            if (fm.findFragmentByTag(TAG_ADD_LINK_DIALOG) != null)
                initAddDialog(dialog)
            //            else if (fm.findFragmentByTag(TAG_ABOUT_DIALOG) != null)
            //                initAboutDialog(dialog);
            //            else if (fm.findFragmentByTag(TAG_TORRENT_SORTING) != null)
            //                initTorrentSortingDialog(dialog);
        }
    }

    private fun initAddDialog(dialog: AlertDialog) {
        val field = dialog.findViewById<TextInputEditText>(R.id.text_input_dialog) ?: return
        val fieldLayout = dialog.findViewById<TextInputLayout>(R.id.layout_text_input_dialog) ?: return

        /* Dismiss error label if user has changed the text */
        field.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                /* Nothing */
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                fieldLayout.isErrorEnabled = false
                fieldLayout.error = null
            }

            override fun afterTextChanged(s: Editable) {
                /* Nothing */
            }
        })

        /*
         * It is necessary in order to the dialog is not closed by
         * pressing positive button if the text checker gave a false result
         */
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

        positiveButton.setOnClickListener { _: View ->
            if (field.text != null) {
                val link = field.text!!.toString()
                if (checkEditTextField(link, fieldLayout)) {
                    val url = when {
                        link.isMagnet() -> link
                        link.isHash() -> INFOHASH_PREFIX + link
                        !link.startsWith(HTTP_PREFIX)
                                && !link.startsWith(HTTPS_PREFIX)
                                && !link.startsWith(UDP_PREFIX) -> "$HTTP_PREFIX://$link"
                        else -> link
                    }
                    dialog.dismiss()
                    addTorrentDialog(Uri.parse(url))
                }
            }
        }

        /* Inserting a link from the clipboard */
        val clipboard = requireActivity().getClipboard() ?: return
        val text = clipboard.toLowerCase(Locale.US)
        if (text.isMagnet() || text.isHash()
            || text.startsWith(HTTP_PREFIX)
            || text.startsWith(HTTPS_PREFIX)) {
            field.setText(clipboard)
            return
        }

        if (field.text.isNullOrEmpty()) {
            field.setText("magnet:?xt=urn:btih:QGN35NKSBO4NUTRQ2CQB67FFA4MVGVUG")
        }
    }

    override fun onPositiveClicked(v: View?) {

    }

    override fun onNegativeClicked(v: View?) {
        val fm = fragmentManager ?: return

        if (fm.findFragmentByTag(TAG_DELETE_TORRENT_DIALOG) != null) {
            selectedTorrents.clear()
        }
    }

    override fun onNeutralClicked(v: View?) {
        /* Nothing */
    }

    private fun addTorrentDialog(uri: Uri) {
        findNavController().navigate(MainFragmentDirections.actionMainFragmentToAddTorrentFragment(uri))
    }

    private fun addTorrent(params: AddTorrentParams) {
        TorrentTaskService.addTorrent(requireActivity(), params)
    }

    companion object {
        private const val TAG_PREV_IMPL_INTENT = "prev_impl_intent"
        private const val TAG_SELECTABLE_ADAPTER = "selectable_adapter"
        private const val TAG_SELECTED_TORRENTS = "selected_torrents"
        private const val TAG_IN_ACTION_MODE = "in_action_mode"
        private const val TAG_DELETE_TORRENT_DIALOG = "delete_torrent_dialog"
        private const val TAG_ADD_LINK_DIALOG = "add_link_dialog"
        private const val TAG_ERROR_OPEN_TORRENT_FILE_DIALOG = "error_open_torrent_file_dialog"
        private const val TAG_SAVE_ERROR_DIALOG = "save_error_dialog"
        private const val TAG_TORRENTS_LIST_STATE = "torrents_list_state"
        private const val TAG_ABOUT_DIALOG = "about_dialog"
        private const val TAG_TORRENT_SORTING = "torrent_sorting"

        private const val ADD_TORRENT_REQUEST = 1
        private const val TORRENT_FILE_CHOOSE_REQUEST = 2
        private const val CREATE_TORRENT_REQUEST = 3
    }
}
