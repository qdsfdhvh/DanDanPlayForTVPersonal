package com.seiko.torrent.ui.add

import android.os.Bundle
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.seiko.common.extensions.lazyAndroid
import com.seiko.torrent.R
import com.seiko.torrent.databinding.TorrentFragmentAddFilesBinding
import com.seiko.torrent.extensions.fixItemAnim

import com.seiko.torrent.model.filetree.BencodeFileTree
import com.seiko.torrent.model.filetree.FileNode
import com.seiko.torrent.vm.AddTorrentViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import kotlin.collections.ArrayList

class AddTorrentFilesFragment : Fragment(), DownloadableFilesAdapter.OnItemClickListener {

    companion object {
        fun newInstance(): AddTorrentFilesFragment {
            return AddTorrentFilesFragment()
        }
    }

    private val viewModel: AddTorrentViewModel by sharedViewModel(from = {
        parentFragment as ViewModelStoreOwner
    })

    private lateinit var binding: TorrentFragmentAddFilesBinding

    private val adapter by lazyAndroid {
        DownloadableFilesAdapter()
    }

    private lateinit var layoutManager: LinearLayoutManager
    private var currentDir: BencodeFileTree? = null

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
    }

    private fun setupUI() {
        adapter.setOnItemClickListener(this)
        layoutManager = LinearLayoutManager(requireContext())
        binding.fileList.layoutManager = layoutManager
        binding.fileList.fixItemAnim()
        binding.fileList.adapter = adapter
        setFileSize(0, 0)
    }

    private fun bindViewModel() {
        // 磁力信息
        viewModel.fileTree.observe(this::getLifecycle) { fileTree ->
            currentDir = fileTree
            adapter.setFiles(getChildren(currentDir))
            updateFileSize()
        }
    }

    override fun onItemClicked(node: BencodeFileTree) {
        if (node.name == BencodeFileTree.PARENT_DIR) {
            backToParent()
        } else if (node.type == FileNode.Type.DIR) {
            chooseDir(node)
        }
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
        val fileTree = viewModel.fileTree.value ?: return
        setFileSize(fileTree.selectedFileSize(), fileTree.size())
    }

    private fun chooseDir(node: BencodeFileTree) {
        val fileTree = viewModel.fileTree.value ?: return
        currentDir = if (node.isFile) fileTree else node
        adapter.setFiles(getChildren(currentDir))
    }

    private fun backToParent() {
        val dir = currentDir ?: return
        currentDir = dir.parent
        adapter.setFiles(getChildren(currentDir))
    }

    private fun getChildren(node: BencodeFileTree?): List<BencodeFileTree> {
        if (node == null || node.isFile) {
            return emptyList()
        }

        val currentDir = currentDir ?: return emptyList()

        val children = ArrayList<BencodeFileTree>()
        if (currentDir != viewModel.fileTree && currentDir.parent != null) {
            children.add(0, BencodeFileTree(
                BencodeFileTree.PARENT_DIR, 0L,
                FileNode.Type.DIR, currentDir.parent)
            )
        }
        children.addAll(currentDir.children)
        return children
    }

}