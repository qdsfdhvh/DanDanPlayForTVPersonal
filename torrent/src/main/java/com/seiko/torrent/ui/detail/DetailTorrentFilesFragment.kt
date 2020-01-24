package com.seiko.torrent.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.seiko.torrent.databinding.TorrentFragmentDetailFileBinding

class DetailTorrentFilesFragment : Fragment() {

    companion object {
        fun newInstance(): DetailTorrentFilesFragment {
            return DetailTorrentFilesFragment()
        }
    }

    private lateinit var binding: TorrentFragmentDetailFileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TorrentFragmentDetailFileBinding.inflate(inflater, container, false)
        return binding.root
    }

}