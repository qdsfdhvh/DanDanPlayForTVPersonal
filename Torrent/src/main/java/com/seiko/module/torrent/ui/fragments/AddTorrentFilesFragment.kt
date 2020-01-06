package com.seiko.module.torrent.ui.fragments

import com.seiko.module.torrent.R

class AddTorrentFilesFragment : BaseFragment() {

    companion object {
        fun newInstance(): AddTorrentFilesFragment {
            return AddTorrentFilesFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.torrent_fragment_add_torrent_files
    }

}