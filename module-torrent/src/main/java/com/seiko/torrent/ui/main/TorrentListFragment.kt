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

import android.os.Bundle
import android.os.Parcelable
import android.view.*
import androidx.fragment.app.Fragment
import androidx.leanback.widget.OnChildViewHolderSelectedListener
import androidx.recyclerview.widget.RecyclerView
import com.seiko.common.eventbus.registerEventBus
import com.seiko.common.eventbus.unRegisterEventBus
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.common.util.toast.toast
import com.seiko.common.ui.adapter.OnItemClickListener
import com.seiko.torrent.R
import com.seiko.torrent.databinding.TorrentFragmentListBinding
import com.seiko.torrent.data.model.PostEvent
import com.seiko.torrent.data.model.TorrentListItem
import com.seiko.torrent.download.Downloader
import com.seiko.torrent.service.TorrentTaskService
import com.seiko.torrent.ui.adapter.TorrentListAdapter
import com.seiko.torrent.vm.MainViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class TorrentListFragment : Fragment()
    , OnItemClickListener {

    /* Save state scrolling */
    private var torrentsListState: Parcelable? = null

    private val viewModel: MainViewModel by sharedViewModel()

    private lateinit var binding: TorrentFragmentListBinding
    private lateinit var adapter: TorrentListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("Navigator - onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = TorrentFragmentListBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Navigator - onViewCreated")
        bindViewModel()
    }

    override fun onStart() {
        super.onStart()
        Timber.d("Navigator - onStart")
        viewModel.loadData(false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        torrentsListState = binding.torrentList.layoutManager?.onSaveInstanceState()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            torrentsListState = savedInstanceState.getParcelable(TAG_TORRENTS_LIST_STATE)
        }
    }

    private fun setupUI() {
        adapter = TorrentListAdapter()
        adapter.setOnItemClickListener(this)
        binding.torrentList.adapter = adapter
        binding.torrentList.setItemSpacing(25)
        binding.torrentList.setPadding(12, 25, 12, 25)
        binding.torrentList.setOnChildViewHolderSelectedListener(mItemSelectedListener)
        setHasOptionsMenu(true)
    }

    private fun bindViewModel() {
        viewModel.torrentItems.observe(this::getLifecycle, adapter::submitList)
    }

    override fun onClick(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        when(holder.itemView.id) {
            R.id.torrent_container_list -> {
                item as TorrentListItem
                val popWindow = TorrentListPopWindow(requireActivity(), item)
                val listener = View.OnClickListener { v ->
                    when(v?.id) {
                        R.id.torrent_btn_play -> {
                            toast("播放")
                        }
                        R.id.torrent_btn_pause -> {
                            viewModel.pauseResumeTorrent(item.hash)
                            popWindow.dismiss()
                        }
                        R.id.torrent_btn_delete -> {
                            TorrentTaskService.delTorrent(requireActivity(), item.hash, true)
                            popWindow.dismiss()
                        }
                    }
                }
//                popWindow.binding.torrentBtnPlay.setOnClickListener(listener)
                popWindow.binding.torrentBtnPause.setOnClickListener(listener)
                popWindow.binding.torrentBtnDelete.setOnClickListener(listener)
                popWindow.show(holder.itemView)
            }
//            R.id.torrent_btn_pause -> {
//                item as TorrentListItem
//                downloader.pauseResumeTorrent(item.hash)
//            }
        }
    }

    /**
     * Item选择监听回调
     */
    private val mItemSelectedListener : OnChildViewHolderSelectedListener by lazyAndroid {
        object : OnChildViewHolderSelectedListener() {
            override fun onChildViewHolderSelected(
                parent: RecyclerView?,
                child: RecyclerView.ViewHolder?,
                position: Int,
                subposition: Int
            ) {
                when(parent) {
                    binding.torrentList -> {
                        val item = adapter.get(position) ?: return
                        if (adapter.isSelectHash(item.hash)) {
                            viewModel.setTorrentHash(null)
                        } else {
                            viewModel.setTorrentHash(item)
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG_TORRENTS_LIST_STATE = "torrents_list_state"
    }
}
