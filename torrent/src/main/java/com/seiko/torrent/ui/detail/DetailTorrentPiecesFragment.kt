package com.seiko.torrent.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.seiko.torrent.databinding.TorrentFragmentDetailPiecesBinding

class DetailTorrentPiecesFragment : Fragment() {
    companion object {
        fun newInstance(): DetailTorrentPiecesFragment {
            return DetailTorrentPiecesFragment()
        }
    }

    private lateinit var binding: TorrentFragmentDetailPiecesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TorrentFragmentDetailPiecesBinding.inflate(inflater, container, false)
        return binding.root
    }

}