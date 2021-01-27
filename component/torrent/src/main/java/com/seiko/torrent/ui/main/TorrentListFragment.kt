package com.seiko.torrent.ui.main

import android.os.Bundle
import android.os.Parcelable
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.leanback.widget.OnChildViewHolderSelectedListener
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.common.util.toast.toast
import com.seiko.common.ui.adapter.OnItemClickListener
import com.seiko.torrent.R
import com.seiko.torrent.databinding.TorrentFragmentListBinding
import com.seiko.torrent.data.model.torrent.TorrentListItem
import com.seiko.torrent.service.TorrentTaskService
import com.seiko.torrent.ui.adapter.TorrentListAdapter
import com.seiko.torrent.vm.TorrentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TorrentListFragment : Fragment(R.layout.torrent_fragment_list)
    , OnItemClickListener {

    companion object {
        private const val TAG_TORRENTS_LIST_STATE = "torrents_list_state"
    }

    /* Save state scrolling */
    private var torrentsListState: Parcelable? = null

    private val viewModel: TorrentViewModel by activityViewModels()

    private val binding: TorrentFragmentListBinding by viewBinding()
    private lateinit var adapter: TorrentListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        bindViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unBindViewModel()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.torrentList.adapter = adapter
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
        binding.torrentList.setItemSpacing(25)
        binding.torrentList.setPadding(12, 25, 12, 25)
        binding.torrentList.setOnChildViewHolderSelectedListener(mItemSelectedListener)
        setHasOptionsMenu(true)
    }

    private fun bindViewModel() {
        viewModel.torrentItems.observe(viewLifecycleOwner, adapter::submitList)
    }

    private fun unBindViewModel() {
        viewModel.torrentItems.removeObservers(this)
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
                popWindow.binding.torrentBtnPause.setOnClickListener(listener)
                popWindow.binding.torrentBtnDelete.setOnClickListener(listener)
                popWindow.show(holder.itemView)
            }
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
                when(parent?.id) {
                    R.id.torrent_list -> {
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

}
