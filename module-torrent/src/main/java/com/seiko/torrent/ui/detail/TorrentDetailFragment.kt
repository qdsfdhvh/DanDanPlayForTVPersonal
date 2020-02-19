package com.seiko.torrent.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.leanback.widget.OnChildViewHolderSelectedListener
import androidx.recyclerview.widget.RecyclerView
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.common.util.toast.toast
import com.seiko.common.ui.adapter.OnItemClickListener
import com.seiko.torrent.R
import com.seiko.torrent.databinding.TorrentFragmentDetailBinding
import com.seiko.torrent.service.TorrentTaskService
import com.seiko.torrent.ui.adapter.TabTitleAdapter
import com.seiko.torrent.vm.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

private const val NUM_FRAGMENTS = 2

private const val FILES_FRAG_POS = 0
private const val INFO_FRAG_POS = 1
//private const val STATE_FRAG_POS = 2
//private const val TRACKERS_FRAG_POS = 3
//private const val PEERS_FRAG_POS = 4
//private const val PIECES_FRAG_POS = 5

class TorrentDetailFragment : Fragment()
    , OnItemClickListener {

    companion object {
        const val TAG = "TorrentDetailFragment"
        private const val ARGS_DETAIL_TAB_SELECTED_POSITION = "ARGS_DETAIL_TAB_SELECTED_POSITION"

        fun newInstance(): TorrentDetailFragment {
            return TorrentDetailFragment()
        }
    }

    private val viewModel: MainViewModel by sharedViewModel()

    private lateinit var binding: TorrentFragmentDetailBinding
    private lateinit var tabAdapter: TabTitleAdapter

    /**
     * 记录位置
     */
    private var tabSelectPosition = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = TorrentFragmentDetailBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkSelectPosition(savedInstanceState)
        bindViewModel()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.torrentTab.adapter = tabAdapter
    }

    override fun onDestroyView() {
        binding.torrentTab.removeOnChildViewHolderSelectedListener(mItemSelectedListener)
        super.onDestroyView()
    }

    /**
     * 保存视图状态
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ARGS_DETAIL_TAB_SELECTED_POSITION, tabSelectPosition)
    }

    private fun checkSelectPosition(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ARGS_DETAIL_TAB_SELECTED_POSITION)) {
                tabSelectPosition = savedInstanceState.getInt(ARGS_DETAIL_TAB_SELECTED_POSITION)
            }
        }
        if (tabSelectPosition >= 0) {
            tabAdapter.setSelectPosition(tabSelectPosition)
        }
    }

    private fun setupUI() {
        tabAdapter = TabTitleAdapter(NUM_FRAGMENTS) { tab, position ->
            tab.setText(when(position) {
                FILES_FRAG_POS -> getString(R.string.torrent_files)
                INFO_FRAG_POS -> getString(R.string.torrent_info)
//                STATE_FRAG_POS -> getString(R.string.torrent_state)
//                TRACKERS_FRAG_POS -> getString(R.string.torrent_trackers)
//                PEERS_FRAG_POS -> getString(R.string.torrent_peers)
//                PIECES_FRAG_POS -> getString(R.string.torrent_pieces)
                else -> ""
            })
        }
        tabAdapter.setOnItemClickListener(this)
        binding.torrentTab.setPadding(25, 0, 25, 0)
        binding.torrentTab.setItemSpacing(25)
        binding.torrentTab.addOnChildViewHolderSelectedListener(mItemSelectedListener)

        // ViewPager2
        binding.torrentViewPager.adapter = DetailPagerAdapter(this)
    }

    private fun bindViewModel() {

    }

    override fun onClick(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        when(holder) {
            is TabTitleAdapter.ViewHolder -> {
                toast("position = $position")
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
                when(parent) {
                    binding.torrentTab -> {
                        if (tabSelectPosition == position) return
                        tabSelectPosition = position
                        tabAdapter.setSelectPosition(position)
                        binding.torrentViewPager.currentItem = position
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.delete_torrent_menu -> {
                val hash = viewModel.torrentItem.value?.hash
                if (hash != null) {
                    TorrentTaskService.delTorrent(requireActivity(), hash, true)
                }
            }
        }
        return true
    }

}

private class DetailPagerAdapter(fragment: Fragment) : FragmentPagerAdapter(fragment.childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int = NUM_FRAGMENTS

    override fun getItem(position: Int): Fragment {
        return when(position) {
            FILES_FRAG_POS -> TorrentDetailFilesFragment.newInstance()
            INFO_FRAG_POS -> TorrentDetailInfoFragment.newInstance()
//            STATE_FRAG_POS -> TorrentDetailStateFragment.newInstance()
//            TRACKERS_FRAG_POS -> TorrentDetailTrackersFragment.newInstance()
//            PEERS_FRAG_POS -> TorrentDetailPeersFragment.newInstance()
//            PIECES_FRAG_POS -> TorrentDetailPiecesFragment.newInstance()
            else -> throw RuntimeException("Can't create fragment with position=$position.")
        }
    }
}