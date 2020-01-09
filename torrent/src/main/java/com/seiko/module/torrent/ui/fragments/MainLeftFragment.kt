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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.seiko.common.eventbus.EventBusScope
import com.seiko.common.extensions.lazyAndroid
import com.seiko.module.torrent.R
import com.seiko.module.torrent.constants.*
import com.seiko.module.torrent.extensions.getClipboard
import com.seiko.module.torrent.extensions.isHash
import com.seiko.module.torrent.extensions.isMagnet
import com.seiko.module.torrent.model.PostEvent
import com.seiko.module.torrent.model.sort.BaseSorting
import com.seiko.module.torrent.model.sort.TorrentSorting
import com.seiko.module.torrent.model.sort.TorrentSortingComparator
import com.seiko.module.torrent.ui.adapters.ToolbarSpinnerAdapter
import com.seiko.module.torrent.ui.adapters.TorrentListAdapter
import com.seiko.module.torrent.model.TorrentListItem
import com.seiko.module.torrent.service.Downloader
import com.seiko.module.torrent.ui.dialogs.BaseAlertDialog
import com.seiko.module.torrent.ui.widgets.RecyclerViewDividerDecoration
import com.seiko.module.torrent.vm.MainViewModel
import kotlinx.android.synthetic.main.torrent_fragment_main_left.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

/*
 * The list of torrents.
 */

class MainLeftFragment : BaseFragment(),
    BaseAlertDialog.OnClickListener,
    BaseAlertDialog.OnDialogShowListener,
    TorrentListAdapter.ClickListener {

    /* Save state scrolling */
    private var torrentsListState: Parcelable? = null
    private var actionMode: ActionMode? = null
    private var inActionMode = false
    private var selectedTorrents: ArrayList<String> = ArrayList()
    private var addTorrentMenu = false

    private val adapter by lazyAndroid {
        TorrentListAdapter(requireActivity(), downloader, this)
    }

    private lateinit var layoutManager: LinearLayoutManager

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

    private val downloader: Downloader by inject()
    private val viewModel: MainViewModel by viewModel()

    override fun getLayoutId(): Int {
        return R.layout.torrent_fragment_main_left
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBusScope.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusScope.unRegister(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)
        bindViewModel()
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.torrentItems.value == null) {
            viewModel.loadData()
        }
    }

    override fun onResume() {
        torrent_list.adapter = adapter
        super.onResume()
        if (torrentsListState != null) {
            layoutManager.onRestoreInstanceState(torrentsListState)
        }
    }

    override fun onPause() {
        super.onPause()
        torrent_list.adapter = null
    }

    private fun setupUI(view: View) {
        toolbar.setTitle(R.string.torrent_title)

        val activity = requireActivity()
        val a = activity.obtainStyledAttributes(TypedValue().data, intArrayOf(R.attr.torrent_divider))
        torrent_list.itemAnimator = animator
        torrent_list.addItemDecoration(RecyclerViewDividerDecoration(a.getDrawable(0)))
        a.recycle()

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

        layoutManager = LinearLayoutManager(activity)
        torrent_list.layoutManager = layoutManager


        /* Show add torrent menu (called from service) after window is displayed */
        requireActivity().window.findViewById<View>(android.R.id.content).post {
            if (addTorrentMenu) {
                /* Hide notification bar */
                activity.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
                addTorrentMenu = false

                val v = activity.window.findViewById<View>(android.R.id.content)
                registerForContextMenu(v)
                activity.openContextMenu(v)
                unregisterForContextMenu(v)
            }
        }
    }

    private fun bindViewModel() {
        viewModel.torrentItems.observe(this::getLifecycle, adapter::addItems)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        torrentsListState = layoutManager.onSaveInstanceState()
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            torrentsListState = savedInstanceState.getParcelable(TAG_TORRENTS_LIST_STATE)
        }
    }

    override fun onItemClicked(position: Int, item: TorrentListItem) {
        if (actionMode == null) {
            /* Mark this torrent as open in the list */
            adapter.markAsOpen(item)
//                    showDetailTorrent(item.torrentId)
        }
    }

    override fun onItemLongClicked(position: Int, item: TorrentListItem): Boolean {
        return true
    }

    override fun onPauseButtonClicked(position: Int, item: TorrentListItem) {
        downloader.pauseResumeTorrent(item.hash)
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

    override fun onShow(dialog: AlertDialog?) {
        if (dialog != null) {
            val fm = fragmentManager ?: return

            if (fm.findFragmentByTag(TAG_ADD_LINK_DIALOG) != null) {
                initAddDialog(dialog)
            }
        }
    }

    private fun initAddDialog(dialog: AlertDialog) {
        val field = dialog.findViewById<TextInputEditText>(R.id.text_input_dialog)!!
        val fieldLayout = dialog.findViewById<TextInputLayout>(R.id.layout_text_input_dialog)!!

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


        // TODO  For DEBUG
        field.setText("magnet:?xt=urn:btih:QGN35NKSBO4NUTRQ2CQB67FFA4MVGVUG")
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceive(event: PostEvent) {
        when(event) {
            is PostEvent.TorrentAdded -> {
                val item  = TorrentListItem(event.torrent)
                adapter.addItem(item)
            }
        }
    }


    companion object {
        private const val TAG_DELETE_TORRENT_DIALOG = "delete_torrent_dialog"
        private const val TAG_ADD_LINK_DIALOG = "add_link_dialog"
        private const val TAG_TORRENTS_LIST_STATE = "torrents_list_state"
    }
}
