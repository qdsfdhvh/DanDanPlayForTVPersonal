package com.seiko.module.torrent.ui.fragments

import android.os.Bundle
import android.text.format.Formatter
import android.view.View
import androidx.lifecycle.ViewModelStoreOwner
import com.seiko.module.torrent.R
import com.seiko.module.torrent.vm.AddTorrentViewModel
import kotlinx.android.synthetic.main.torrent_add_torrent_info.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.text.SimpleDateFormat
import java.util.*

class AddTorrentInfoFragment : BaseFragment() {

    companion object {
        fun newInstance(): AddTorrentInfoFragment {
            return AddTorrentInfoFragment()
        }
    }

    private var customName = ""

    private val viewModel by sharedViewModel<AddTorrentViewModel>(from = {
        parentFragment as ViewModelStoreOwner
    })

    override fun getLayoutId(): Int {
        return R.layout.torrent_fragment_add_torrent_info
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel.downloadDir.observe(this::getLifecycle) { downloadDir ->
            upload_torrent_into.text = downloadDir.absolutePath
        }
        viewModel.torrentMetaInfo.observe(this::getLifecycle) { info ->
            torrent_name.setText(if (customName.isEmpty()) info.torrentName else customName)
            torrent_hash_sum.text = info.sha1Hash

            if (info.comment.isEmpty()) {
                layout_torrent_comment.visibility = View.GONE
            } else {
                layout_torrent_comment.visibility = View.VISIBLE
                torrent_comment.text = info.comment
            }

            if (info.createdBy.isEmpty()) {
                layout_torrent_created_in_program.visibility = View.GONE
            } else {
                layout_torrent_created_in_program.visibility = View.VISIBLE
                torrent_created_in_program.text = info.createdBy
            }

            if (info.torrentSize == 0L || info.fileCount == 0) {
                layout_torrent_size_and_count.visibility = View.GONE
            } else {
                layout_torrent_size_and_count.visibility = View.VISIBLE

                torrent_size.text = Formatter.formatFileSize(requireActivity(), info.torrentSize)
                torrent_file_count.text = "%d".format(info.fileCount)

                val freeSpace = viewModel.downloadDir.value?.usableSpace ?: 0L
                torrent_size.text = getString(R.string.torrent_free_space).format(
                    Formatter.formatFileSize(requireActivity(), freeSpace))
            }

            if (info.creationDate == 0L) {
                layout_torrent_create_date.visibility = View.GONE
            } else {
                layout_torrent_create_date.visibility = View.VISIBLE

                torrent_create_date.text = SimpleDateFormat.getDateTimeInstance()
                    .format(Date(info.creationDate))
            }
        }
    }

}