package com.seiko.torrent.ui.detail

import com.seiko.torrent.R
import com.seiko.torrent.ui.base.BaseFragment

class DetailTorrentStateFragment : BaseFragment() {

    companion object {
        fun newInstance(): DetailTorrentStateFragment {
            return DetailTorrentStateFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.torrent_fragment_detail_state
    }
}