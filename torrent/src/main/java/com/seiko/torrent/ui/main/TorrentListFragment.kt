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

package com.seiko.torrent.ui.main

import android.app.Activity
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
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils

import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.seiko.common.eventbus.EventBusScope
import com.seiko.common.extensions.lazyAndroid
import com.seiko.torrent.R
import com.seiko.torrent.constants.*
import com.seiko.torrent.extensions.fixItemAnim
import com.seiko.torrent.extensions.getClipboard
import com.seiko.torrent.extensions.isHash
import com.seiko.torrent.extensions.isMagnet
import com.seiko.torrent.model.PostEvent
import com.seiko.torrent.model.TorrentListItem
import com.seiko.torrent.service.Downloader
import com.seiko.torrent.ui.base.BaseFragment
import com.seiko.torrent.ui.dialog.BaseAlertDialog
import com.seiko.torrent.ui.widget.RecyclerViewDividerDecoration
import com.seiko.torrent.vm.MainViewModel
import kotlinx.android.synthetic.main.torrent_fragment_list.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

/*
 * The list of torrents.
 */

class TorrentListFragment : BaseFragment(),
    TorrentListAdapter.ClickListener {

    /* Save state scrolling */
    private var torrentsListState: Parcelable? = null

    private val adapter by lazyAndroid {
        TorrentListAdapter(requireActivity(), downloader, this)
    }

    private lateinit var layoutManager: LinearLayoutManager

    private val downloader: Downloader by inject()
    private val viewModel: MainViewModel by sharedViewModel()

    override fun getLayoutId(): Int {
        return R.layout.torrent_fragment_list
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBusScope.register(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        bindViewModel()
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadData(false)
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

    override fun onDestroy() {
        EventBusScope.unRegister(this)
        super.onDestroy()
    }

    private fun setupUI() {
        toolbar.setTitle(R.string.torrent_title)

        val activity = requireActivity()
        torrent_list.fixItemAnim()
        torrent_list.addItemDecoration(RecyclerViewDividerDecoration(requireContext(), R.drawable.torrent_table_mode_divider))

        setHasOptionsMenu(true)

        layoutManager = LinearLayoutManager(activity)
        torrent_list.layoutManager = layoutManager

    }

    private fun bindViewModel() {
        viewModel.torrentItems.observe(this::getLifecycle, adapter::addItems)
        viewModel.torrentItem.observe(this::getLifecycle) { item ->
            adapter.markAsOpen(item?.hash)
        }
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
        if (adapter.isSelectHash(item.hash)) {
            viewModel.setTorrentHash(null)
        } else {
            viewModel.setTorrentHash(item)
        }
    }

    override fun onItemLongClicked(position: Int, item: TorrentListItem): Boolean {
        return true
    }

    override fun onPauseButtonClicked(position: Int, item: TorrentListItem) {
        downloader.pauseResumeTorrent(item.hash)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceive(event: PostEvent) {
        when(event) {
            is PostEvent.TorrentAdded -> {
                LogUtils.d("Get Torrent Added: ${event.torrent.hash}")
                val item = TorrentListItem(event.torrent)
                adapter.addItem(item)
            }
            is PostEvent.TorrentRemoved -> {
                val hash = event.hash
                adapter.deleteItem(hash)
                if (adapter.isSelectHash(hash)) {
                    viewModel.setTorrentHash(null)
                }
            }
        }
    }

    companion object {
        private const val TAG_TORRENTS_LIST_STATE = "torrents_list_state"
    }
}
