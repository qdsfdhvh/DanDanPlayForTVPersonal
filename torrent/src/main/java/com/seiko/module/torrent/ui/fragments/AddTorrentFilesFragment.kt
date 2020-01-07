package com.seiko.module.torrent.ui.fragments

import android.os.Bundle
import android.text.format.Formatter
import android.view.View
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.seiko.common.extensions.lazyAndroid
import com.seiko.module.torrent.R

import com.seiko.module.torrent.model.filetree.BencodeFileTree
import com.seiko.module.torrent.model.filetree.FileNode
import com.seiko.module.torrent.model.filetree.FileTree
import com.seiko.module.torrent.ui.adapters.DownloadableFilesAdapter
import com.seiko.module.torrent.vm.AddTorrentViewModel
import com.seiko.torrent.model.BencodeFileItem
import kotlinx.android.synthetic.main.torrent_fragment_add_torrent_files.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.libtorrent4j.Priority
import java.io.File
import java.util.*
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

    private lateinit var layoutManager: LinearLayoutManager

    private val adapter by lazyAndroid {
        DownloadableFilesAdapter(requireActivity(),
            R.layout.torrent_item_torrent_downloadable_file,
            this)
    }

    private val animator = object : DefaultItemAnimator() {
        override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
            return true
        }
    }

    private var files: List<BencodeFileItem>? = null
    private var priorities: List<Priority>? = null
    private var fileTree: BencodeFileTree? = null

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
        viewModel.torrentMetaInfo.observe(this::getLifecycle) { info ->
            if (files == null || files != info.fileList) {
                files = info.fileList
                makeFileTree()
            }
        }
        // 磁力信息
        viewModel.magnetInfo.observe(this::getLifecycle) { info ->
            if (priorities == null || priorities != info?.filePriorities) {
                priorities = info?.filePriorities
                makeFileTree()
            }
        }
    }

    private fun initViews() {
        layoutManager = LinearLayoutManager(requireActivity())
        file_list.layoutManager = layoutManager
        file_list.itemAnimator = animator
        file_list.adapter = adapter
    }

    private fun makeFileTree() {
        val files = files ?: return

        val fileTree = files.toFileTree()

        val priorities = priorities
        if (priorities.isNullOrEmpty()) {
            fileTree.select(true)
        } else {
            val size = priorities.size.coerceAtMost(files.size)
            for (i in 0 until size) {
                if (priorities[i] == Priority.IGNORE) {
                    continue
                }
                val file = fileTree.find(i) ?: continue
                file.select(true)
            }
        }

        this.fileTree = fileTree
        currentDir = fileTree
        adapter.setFiles(getChildren(currentDir))
        updateFileSize()
    }

    private fun updateFileSize() {
        val fileTree = fileTree ?: return
        files_size.text = getString(R.string.torrent_files_size).format(
            Formatter.formatFileSize(requireActivity().applicationContext, fileTree.selectedFileSize()),
            Formatter.formatFileSize(requireActivity().applicationContext, fileTree.size())
        )
    }

    private fun chooseDir(node: BencodeFileTree) {
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
        if (currentDir != fileTree && currentDir.parent != null) {
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


private fun List<BencodeFileItem>.toFileTree(): BencodeFileTree {
    val root = BencodeFileTree(FileTree.ROOT, 0L, FileNode.Type.DIR)
    var parentTree = root
    /* It allows reduce the number of iterations on the paths with equal beginnings */
    var prevPath = ""
    val filesCopy = ArrayList(this)
    /* Sort reduces the returns number to root */
    filesCopy.sort()

    for (file in filesCopy) {
        val path: String
        /*
         * Compare previous path with new path.
         * Example:
         * prev = dir1/dir2/
         * cur  = dir1/dir2/file1
         *        |________|
         *          equal
         *
         * prev = dir1/dir2/
         * cur  = dir3/file2
         *        |________|
         *         not equal
         */
        if (prevPath.isNotEmpty()
            && file.path.regionMatches(
                0, prevPath,
                0, prevPath.length,
                true)) {
            /*
             * If beginning paths are equal, remove previous path from the new path.
             * Example:
             * prev = dir1/dir2/
             * cur  = dir1/dir2/file1
             * new  = file1
             */
            path = file.path.substring(prevPath.length)
        } else {
            /* If beginning paths are not equal, return to root */
            path = file.path
            parentTree = root
        }

        val nodes = path.parsePath()
        /*
         * Remove last node (file) from previous path.
         * Example:
         * cur = dir1/dir2/file1
         * new = dir1/dir2/
         */
        prevPath = file.path.substring(0,
            file.path.length - nodes[nodes.size - 1].length)

        /* Iterates path nodes */
        nodes.forEachIndexed { i, node ->
            if (!parentTree.contains(node)) {
                parentTree.addChild(makeObject(
                    index = file.index,
                    name = node,
                    size = file.size,
                    parent = parentTree,
                    isFile = i == nodes.size - 1
                ))
            }

            val nextParent = parentTree.getChild(node)
            /* Skipping leaf nodes */
            if (!nextParent.isFile) {
                parentTree = nextParent
            }
        }
    }
    return root
}

private fun makeObject(index: Int,
                       name: String,
                       size: Long,
                       parent: BencodeFileTree,
                       isFile: Boolean): BencodeFileTree {
    return if (isFile) {
        BencodeFileTree(index, name, size, FileNode.Type.FILE, parent)
    } else {
        BencodeFileTree(name, 0L, FileNode.Type.DIR, parent)
    }
}

private fun String.parsePath(): List<String> {
    if (isNullOrEmpty()) return emptyList()
    return split(File.separator)
}

/*
 * Not recommended for use with a large number of nodes
 * due to deterioration of performance, use getLeaves() method
 */
private fun <F : FileTree<F>> F.find(index: Int): F? {
    val stack = Stack<F>()
    stack.push(this)
    while (stack.isNotEmpty()) {
        val node = stack.pop() ?: continue
        if (node.index == index) {
            return node
        } else {
            for (n in node.childrenName) {
                if (!node.isFile) {
                    stack.push(node.getChild(n))
                }
            }
        }
    }
    return null
}