package com.seiko.torrent.ui.detail

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.tabs.TabLayoutMediator
import com.seiko.common.eventbus.EventBusScope
import com.seiko.torrent.R
import com.seiko.torrent.model.PostEvent
import com.seiko.torrent.service.TorrentTaskService
import com.seiko.torrent.ui.base.BaseFragment
import com.seiko.torrent.vm.MainViewModel
import kotlinx.android.synthetic.main.torrent_fragment_detail.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class DetailTorrentFragment : BaseFragment() {

    private val viewModel: MainViewModel by sharedViewModel()

    override fun getLayoutId(): Int {
        return R.layout.torrent_fragment_detail
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        bindViewModel()
    }

    private fun setupUI() {
        detail_toolbar.inflateMenu(R.menu.torrent_detail_torrent)
        detail_toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected)

        // ViewPager2
        detail_torrent_viewpager.adapter = DetailPagerAdapter(this)
        TabLayoutMediator(detail_torrent_tabs, detail_torrent_viewpager) { tab, position ->
            tab.text = when(position) {
                INFO_FRAG_POS -> getString(R.string.torrent_info)
                STATE_FRAG_POS -> getString(R.string.torrent_state)
                FILES_FRAG_POS -> getString(R.string.torrent_files)
                TRACKERS_FRAG_POS -> getString(R.string.torrent_trackers)
                PEERS_FRAG_POS -> getString(R.string.torrent_peers)
                PIECES_FRAG_POS -> getString(R.string.torrent_pieces)
                else -> ""
            }
        }.attach()
    }

    private fun bindViewModel() {

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

private const val NUM_FRAGMENTS = 6
private const val INFO_FRAG_POS = 0
private const val STATE_FRAG_POS = 1
private const val FILES_FRAG_POS = 2
private const val TRACKERS_FRAG_POS = 3
private const val PEERS_FRAG_POS = 4
private const val PIECES_FRAG_POS = 5

private class DetailPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = NUM_FRAGMENTS

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            INFO_FRAG_POS -> DetailTorrentInfoFragment.newInstance()
            STATE_FRAG_POS -> DetailTorrentStateFragment.newInstance()
            FILES_FRAG_POS -> DetailTorrentFilesFragment.newInstance()
            TRACKERS_FRAG_POS -> DetailTorrentTrackersFragment.newInstance()
            PEERS_FRAG_POS -> DetailTorrentPeersFragment.newInstance()
            PIECES_FRAG_POS -> DetailTorrentPiecesFragment.newInstance()
            else -> throw RuntimeException("Can't create fragment with position=$position.")
        }
    }

}