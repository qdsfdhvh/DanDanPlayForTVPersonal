package com.seiko.torrent.ui.add

import android.os.Bundle
import android.text.format.Formatter
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.common.util.extensions.parentViewModels
import com.seiko.torrent.R
import com.seiko.torrent.databinding.TorrentFragmentAddFilesBinding
import com.seiko.torrent.util.extensions.fixItemAnim
import com.seiko.torrent.data.model.filetree.BencodeFileTree
import com.seiko.torrent.vm.AddTorrentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TorrentAddFilesFragment : Fragment(R.layout.torrent_fragment_add_files), DownloadableFilesAdapter.OnItemClickListener {

    companion object {
        fun newInstance(): TorrentAddFilesFragment {
            return TorrentAddFilesFragment()
        }
    }

    private val viewModel: AddTorrentViewModel by parentViewModels()

    private val binding: TorrentFragmentAddFilesBinding by viewBinding()

    private val adapter by lazyAndroid { DownloadableFilesAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        bindViewModel()
    }

    override fun onDestroyView() {
        adapter.setOnItemClickListener(null)
        super.onDestroyView()
        unBindViewModel()
    }

    private fun setupUI() {
        adapter.setOnItemClickListener(this)
        binding.fileList.fixItemAnim()
        binding.fileList.adapter = adapter
        setFileSize(0, 0)
    }

    private fun bindViewModel() {
        // 磁力信息
        viewModel.fileTree.observe(viewLifecycleOwner) { fileTree ->
            adapter.setFileTree(fileTree)
            updateFileSize()
        }
    }

    private fun unBindViewModel() {
        viewModel.fileTree.removeObservers(this)
    }

    override fun onItemClicked(node: BencodeFileTree) {

    }

    override fun onItemCheckedChanged(node: BencodeFileTree, selected: Boolean) {
        node.select(selected)
        updateFileSize()
    }

    private fun setFileSize(selectedSize: Long, totalSize: Long) {
        binding.filesSize.text = getString(R.string.torrent_files_size).format(
            Formatter.formatFileSize(requireActivity().applicationContext, selectedSize),
            Formatter.formatFileSize(requireActivity().applicationContext, totalSize)
        )
    }

    private fun updateFileSize() {
        val fileTree = adapter.getFileTree() ?: return
        setFileSize(fileTree.selectedFileSize(), fileTree.size())
    }

}