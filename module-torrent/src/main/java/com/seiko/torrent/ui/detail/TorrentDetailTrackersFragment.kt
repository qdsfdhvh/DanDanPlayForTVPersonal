package com.seiko.torrent.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.seiko.torrent.databinding.TorrentFragmentDetailTrackersBinding

class TorrentDetailTrackersFragment : Fragment() {

    companion object {
        fun newInstance(): TorrentDetailTrackersFragment {
            return TorrentDetailTrackersFragment()
        }
    }

    private lateinit var binding: TorrentFragmentDetailTrackersBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TorrentFragmentDetailTrackersBinding.inflate(inflater, container, false)
        return binding.root
    }

}