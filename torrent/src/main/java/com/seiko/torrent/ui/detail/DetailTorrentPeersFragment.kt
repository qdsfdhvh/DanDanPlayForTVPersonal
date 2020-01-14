package com.seiko.torrent.ui.detail

import com.seiko.torrent.R
import com.seiko.torrent.ui.base.BaseFragment

class DetailTorrentPeersFragment : BaseFragment() {

    companion object {
        fun newInstance(): DetailTorrentPeersFragment {
            return DetailTorrentPeersFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.torrent_fragment_detail_peers
    }
}