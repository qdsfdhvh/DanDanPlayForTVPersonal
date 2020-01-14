package com.seiko.torrent.ui.detail

import com.seiko.torrent.R
import com.seiko.torrent.ui.base.BaseFragment

class DetailTorrentFilesFragment : BaseFragment() {

    companion object {
        fun newInstance(): DetailTorrentFilesFragment {
            return DetailTorrentFilesFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.torrent_fragment_detail_file
    }
}