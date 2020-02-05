package com.seiko.torrent.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.seiko.torrent.databinding.TorrentFragmentDetailPeersBinding

class DetailTorrentPeersFragment : Fragment() {

    companion object {
        fun newInstance(): DetailTorrentPeersFragment {
            return DetailTorrentPeersFragment()
        }
    }

    private lateinit var binding: TorrentFragmentDetailPeersBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TorrentFragmentDetailPeersBinding.inflate(inflater, container, false)
        return binding.root
    }

}