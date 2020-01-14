package com.seiko.torrent.ui.detail

import android.os.Bundle
import android.text.format.Formatter
import android.view.View
import com.seiko.torrent.R
import com.seiko.torrent.ui.base.BaseFragment
import com.seiko.torrent.vm.MainViewModel
import kotlinx.android.synthetic.main.torrent_fragment_detail_info.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class DetailTorrentInfoFragment : BaseFragment() {

    companion object {
        fun newInstance(): DetailTorrentInfoFragment {
            return DetailTorrentInfoFragment()
        }
    }

    private val viewModel: MainViewModel by sharedViewModel()

    override fun getLayoutId(): Int {
        return R.layout.torrent_fragment_detail_info
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun setupUI() {
        folder_chooser_button.setOnClickListener {

        }
    }

    private fun bindViewModel() {
        viewModel.torrentItem.observe(this::getLifecycle) { item ->
            if (item == null) {
                upload_torrent_into.text = ""
                free_space.text = ""
                torrent_added.text = ""
            } else {
                upload_torrent_into.text = item.downloadPath
                free_space.text = getString(R.string.torrent_free_space).format(
                    Formatter.formatFileSize(requireActivity(), File(item.downloadPath).usableSpace)
                )
                torrent_added.text = SimpleDateFormat.getDateTimeInstance()
                    .format(Date(item.dateAdded))
            }
        }
        viewModel.torrentMetaInfo.observe(this::getLifecycle) { info ->
            if (info == null) {
                torrent_name.setText("")
                torrent_hash_sum.text = ""
                torrent_comment.text = ""
                torrent_created_in_program.text = ""
                torrent_size.text = ""
                torrent_create_date.text = ""
            } else {
                torrent_name.setText(info.torrentName)
                torrent_hash_sum.text = info.sha1Hash
                torrent_comment.text = info.comment
                torrent_created_in_program.text = info.createdBy
                torrent_size.text = Formatter.formatFileSize(requireActivity(), info.torrentSize)
                torrent_file_count.text = "%d".format(info.fileCount)
                torrent_create_date.text = SimpleDateFormat.getDateTimeInstance()
                    .format(Date(info.creationDate))
            }
        }
    }

}