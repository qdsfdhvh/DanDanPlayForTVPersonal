package com.seiko.torrent.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.leanback.widget.OnChildViewHolderSelectedListener
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.common.util.toast.toast
import com.seiko.common.ui.adapter.OnItemClickListener
import com.seiko.torrent.R
import com.seiko.torrent.databinding.TorrentFragmentDetailBinding
import com.seiko.torrent.service.TorrentTaskService
import com.seiko.torrent.ui.adapter.TabTitleAdapter
import com.seiko.torrent.vm.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

private const val NUM_FRAGMENTS = 6

private const val INFO_FRAG_POS = 0
private const val FILES_FRAG_POS = 1
private const val STATE_FRAG_POS = 2
private const val TRACKERS_FRAG_POS = 3
private const val PEERS_FRAG_POS = 4
private const val PIECES_FRAG_POS = 5

class DetailTorrentFragment : Fragment()
    , OnItemClickListener {

    companion object {
        private const val ARGS_DETAIL_TAB_SELECTED_POSITION = "ARGS_DETAIL_TAB_SELECTED_POSITION"
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
                INFO_FRAG_POS -> getString(R.string.torrent_info)
                FILES_FRAG_POS -> getString(R.string.torrent_files)
                STATE_FRAG_POS -> getString(R.string.torrent_state)
                TRACKERS_FRAG_POS -> getString(R.string.torrent_trackers)
                PEERS_FRAG_POS -> getString(R.string.torrent_peers)
                PIECES_FRAG_POS -> getString(R.string.torrent_pieces)
                else -> ""
            })
        }
        tabAdapter.setOnItemClickListener(this)
        binding.torrentTab.setPadding(25, 0, 25, 0)
        binding.torrentTab.setItemSpacing(25)
        binding.torrentTab.addOnChildViewHolderSelectedListener(mItemSelectedListener)
        binding.torrentTab.adapter = tabAdapter

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
                        Timber.d("select position = $position")
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

private class DetailPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = NUM_FRAGMENTS

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            INFO_FRAG_POS -> DetailTorrentInfoFragment.newInstance()
            FILES_FRAG_POS -> DetailTorrentFilesFragment.newInstance()
            STATE_FRAG_POS -> DetailTorrentStateFragment.newInstance()
            TRACKERS_FRAG_POS -> DetailTorrentTrackersFragment.newInstance()
            PEERS_FRAG_POS -> DetailTorrentPeersFragment.newInstance()
            PIECES_FRAG_POS -> DetailTorrentPiecesFragment.newInstance()
            else -> throw RuntimeException("Can't create fragment with position=$position.")
        }
    }

}