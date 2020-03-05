package com.seiko.torrent.ui.add

import android.os.Bundle
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.observe
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.torrent.R
import com.seiko.torrent.databinding.TorrentFragmentAddFilesBinding
import com.seiko.torrent.util.extensions.fixItemAnim

import com.seiko.torrent.data.model.filetree.BencodeFileTree
import com.seiko.torrent.vm.AddTorrentViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

class TorrentAddFilesFragment : Fragment(), DownloadableFilesAdapter.OnItemClickListener {

    companion object {
        fun newInstance(): TorrentAddFilesFragment {
            return TorrentAddFilesFragment()
        }
    }

    private val viewModel: AddTorrentViewModel by sharedViewModel(from = {
        parentFragment as ViewModelStoreOwner
    })

    private lateinit var binding: TorrentFragmentAddFilesBinding

    private val adapter by lazyAndroid { DownloadableFilesAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TorrentFragmentAddFilesBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        viewModel.fileTree.observe(this) { fileTree ->
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