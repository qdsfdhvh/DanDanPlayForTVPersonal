package com.seiko.torrent.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.seiko.torrent.databinding.TorrentFragmentDetailStateBinding

class TorrentDetailStateFragment : Fragment() {

    companion object {
        fun newInstance(): TorrentDetailStateFragment {
            return TorrentDetailStateFragment()
        }
    }

    private lateinit var binding: TorrentFragmentDetailStateBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TorrentFragmentDetailStateBinding.inflate(inflater, container, false)
        return binding.root
    }

}