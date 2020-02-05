package com.seiko.torrent.ui.detail

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.seiko.common.router.Navigator
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.common.util.toast.toast
import com.seiko.torrent.data.model.filetree.BencodeFileTree
import com.seiko.torrent.data.model.filetree.FileNode
import com.seiko.torrent.databinding.TorrentFragmentDetailFileBinding
import com.seiko.torrent.util.extensions.fixItemAnim
import com.seiko.torrent.util.extensions.toFileTree
import com.seiko.torrent.vm.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.io.File

class DetailTorrentFilesFragment : Fragment(), DetailTorrentFilesAdapter.OnItemClickListener {

    companion object {
        fun newInstance(): DetailTorrentFilesFragment {
            return DetailTorrentFilesFragment()
        }
    }

    private val viewModel: MainViewModel by sharedViewModel()

    private lateinit var binding: TorrentFragmentDetailFileBinding

    private val adapter by lazyAndroid { DetailTorrentFilesAdapter() }

    private var currentDir: BencodeFileTree? = null
    private var fileTree: BencodeFileTree? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TorrentFragmentDetailFileBinding.inflate(inflater, container, false)
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
        binding.fileList.fixItemAnim()
        binding.fileList.adapter = adapter
    }

    private fun bindViewModel() {
        viewModel.torrentMetaInfo.observe(this::getLifecycle) { info ->
            if (info == null) {
                return@observe
            }
            fileTree = info.fileList.toFileTree()
            currentDir = fileTree
            adapter.setFiles(getChildren(currentDir))
        }
    }

    override fun onItemClicked(node: BencodeFileTree) {
        when {
            node.name == BencodeFileTree.PARENT_DIR -> {
                backToParent()
            }
            node.type == FileNode.Type.DIR -> {
                chooseDir(node)
            }
            node.type == FileNode.Type.FILE -> {
                val item = viewModel.torrentItem.value ?: return
                val file = File(item.downloadPath, node.path)
                if (!file.exists()) {
                    toast("文件不存在：${file.absolutePath}")
                    return
                }
                Timber.d(file.absolutePath)
                Navigator.navToPlayer(this, Uri.fromFile(file), node.name, item.hash)
            }
        }
    }

    private fun chooseDir(node: BencodeFileTree) {
        val fileTree = fileTree ?: return
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
}