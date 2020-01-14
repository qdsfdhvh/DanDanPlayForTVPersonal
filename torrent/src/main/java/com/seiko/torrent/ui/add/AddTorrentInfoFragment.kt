package com.seiko.torrent.ui.add

import android.os.Bundle
import android.text.format.Formatter
import android.view.View
import androidx.lifecycle.ViewModelStoreOwner
import com.seiko.torrent.R
import com.seiko.torrent.ui.base.BaseFragment
import com.seiko.torrent.vm.AddTorrentViewModel
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

    private val viewModel: AddTorrentViewModel by sharedViewModel(from = {
        parentFragment as ViewModelStoreOwner
    })

    override fun getLayoutId(): Int {
        return R.layout.torrent_fragment_add_torrent_info
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        bindViewModel()
    }

    private fun setupUI() {
        start_torrent.isChecked = viewModel.autoStart
        start_torrent.setOnCheckedChangeListener { _, isChecked ->
            viewModel.autoStart = isChecked
        }
        sequential_download.isChecked = viewModel.isSequentialDownload
        sequential_download.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isSequentialDownload = isChecked
        }
    }

    private fun bindViewModel() {
        viewModel.downloadDir.observe(this::getLifecycle) { downloadDir ->
            upload_torrent_into.text = downloadDir.absolutePath
        }
        // 磁力信息
        viewModel.magnetInfo.observe(this::getLifecycle) { info ->
            if (info == null) return@observe
            torrent_name.setText(if (viewModel.customName.isEmpty()) info.name else viewModel.customName)
            torrent_hash_sum.text = info.sha1hash

            layout_torrent_size_and_count.visibility = View.GONE
            layout_torrent_create_date.visibility = View.GONE
            layout_torrent_comment.visibility = View.GONE
            layout_torrent_created_in_program.visibility = View.GONE
        }
        // 种子信息
        viewModel.torrentMetaInfo.observe(this::getLifecycle) { info ->
            torrent_name.setText(if (viewModel.customName.isEmpty()) info.torrentName else viewModel.customName)
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
                free_space.text = getString(R.string.torrent_free_space).format(
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