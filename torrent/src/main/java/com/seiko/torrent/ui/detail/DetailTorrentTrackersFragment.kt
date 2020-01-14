package com.seiko.torrent.ui.detail

import com.seiko.torrent.R
import com.seiko.torrent.ui.base.BaseFragment

class DetailTorrentTrackersFragment : BaseFragment() {

    companion object {
        fun newInstance(): DetailTorrentTrackersFragment {
            return DetailTorrentTrackersFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.torrent_fragment_detail_trackers
    }
}