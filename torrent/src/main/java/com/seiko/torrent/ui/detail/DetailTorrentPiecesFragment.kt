package com.seiko.torrent.ui.detail

import com.seiko.torrent.R
import com.seiko.torrent.ui.base.BaseFragment

class DetailTorrentPiecesFragment : BaseFragment() {
    companion object {
        fun newInstance(): DetailTorrentPiecesFragment {
            return DetailTorrentPiecesFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.torrent_fragment_detail_pieces
    }
}