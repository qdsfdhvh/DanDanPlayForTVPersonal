package com.seiko.torrent.ui.add

import android.os.Bundle
import android.text.format.Formatter
import android.view.View
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seiko.common.extensions.lazyAndroid
import com.seiko.torrent.R
import com.seiko.torrent.extensions.fixItemAnim

import com.seiko.torrent.model.filetree.BencodeFileTree
import com.seiko.torrent.model.filetree.FileNode
import com.seiko.torrent.ui.base.BaseFragment
import com.seiko.torrent.vm.AddTorrentViewModel
import kotlinx.android.synthetic.main.torrent_fragment_add_torrent_files.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import kotlin.collections.ArrayList

class AddTorrentFilesFragment : BaseFragment(), DownloadableFilesAdapter.ViewHolder.ClickListener {

    companion object {
        fun newInstance(): AddTorrentFilesFragment {
            return AddTorrentFilesFragment()
        }
    }

    private val viewModel: AddTorrentViewModel by sharedViewModel(from = {
        parentFragment as ViewModelStoreOwner
    })

    private val adapter by lazyAndroid {
        DownloadableFilesAdapter(requireActivity(), this)
    }

    private lateinit var layoutManager: LinearLayoutManager

    private var currentDir: BencodeFileTree? = null

    override fun getLayoutId(): Int {
        return R.layout.torrent_fragment_add_torrent_files
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initViews()
    }

    private fun initViewModel() {
        // 磁力信息
        viewModel.fileTree.observe(this::getLifecycle) { fileTree ->
            currentDir = fileTree
            adapter.setFiles(getChildren(currentDir))
            updateFileSize()
        }
    }

    private fun initViews() {
        layoutManager = LinearLayoutManager(requireActivity())
        file_list.layoutManager = layoutManager
        file_list.fixItemAnim()
        file_list.adapter = adapter
        setFileSize(0, 0)
    }

    private fun setFileSize(selectedSize: Long, totalSize: Long) {
        files_size.text = getString(R.string.torrent_files_size).format(
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

}