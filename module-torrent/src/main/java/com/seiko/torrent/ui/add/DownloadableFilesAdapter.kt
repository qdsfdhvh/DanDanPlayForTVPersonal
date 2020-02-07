package com.seiko.torrent.ui.add

import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seiko.torrent.R
import com.seiko.torrent.databinding.TorrentItemDownloadableFileBinding
import com.seiko.torrent.data.model.filetree.BencodeFileTree
import com.seiko.torrent.data.model.filetree.FileNode
import java.util.*

class DownloadableFilesAdapter : RecyclerView.Adapter<DownloadableFilesAdapter.ViewHolder>() {


    private var currentDir: BencodeFileTree? = null
    private var fileTree: BencodeFileTree? = null
    private var files = ArrayList<BencodeFileTree>()

    fun getFileTree() = fileTree

    /**
     * 修改文件书
     */
    fun setFileTree(fileTree: BencodeFileTree) {
        this.fileTree = fileTree
        currentDir = fileTree
        setFiles(getChildren(currentDir))
    }

    /**
     * 选择目录
     */
    private fun chooseDir(node: BencodeFileTree) {
        val fileTree = fileTree ?: return
        currentDir = if (node.isFile) fileTree else node
        setFiles(getChildren(currentDir))
    }

    /**
     * 返回上一层目录
     */
    private fun backToParent() {
        val dir = currentDir ?: return
        currentDir = dir.parent
        setFiles(getChildren(currentDir))
    }

    /**
     * 获得此目录下载的信息
     */
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

    /**
     * 切换当前显示的文件目录
     */
    private fun setFiles(list: Collection<BencodeFileTree>) {
        if (files.isNotEmpty()) {
            files.clear()
        }
        files.addAll(list)
        files.sort()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return files.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TorrentItemDownloadableFileBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClicked(node: BencodeFileTree)
        fun onItemCheckedChanged(node: BencodeFileTree, selected: Boolean)
    }

    inner class ViewHolder(
        private val binding: TorrentItemDownloadableFileBinding
    ) : RecyclerView.ViewHolder(binding.root)
        , View.OnClickListener
        , View.OnFocusChangeListener {

        init {
            binding.root.isFocusable = true
            binding.root.isFocusableInTouchMode = true
            binding.root.onFocusChangeListener = this
            binding.root.setOnClickListener(this)
        }

        fun bind(position: Int) {
            val file = files[position]

            binding.torrentFileName.text = file.name
            binding.torrentFileIcon.setImageResource(if (file.type == FileNode.Type.DIR) {
                R.drawable.torrent_ic_folder_grey600_24dp
            } else {
                R.drawable.torrent_ic_file_grey600_24dp
            })

            if (file.name == BencodeFileTree.PARENT_DIR) {
                binding.torrentFileSelected.visibility = View.GONE
                binding.torrentFileSize.visibility = View.GONE
            } else {
                binding.torrentFileSelected.visibility = View.VISIBLE
                binding.torrentFileSelected.isChecked = file.isSelected
                binding.torrentFileSize.visibility = View.VISIBLE
                binding.torrentFileSize.text = Formatter.formatFileSize(
                    binding.root.context,
                    file.size())
            }
        }

        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            v?.isSelected = hasFocus
        }

        override fun onClick(v: View) {
            val position = adapterPosition
            if (position < 0) return

            val node = files[position]
            when {
                node.name == BencodeFileTree.PARENT_DIR -> {
                    backToParent()
                }
                node.type == FileNode.Type.DIR -> {
                    chooseDir(node)
                }
                node.type == FileNode.Type.FILE -> {
                    binding.torrentFileSelected.isChecked = !binding.torrentFileSelected.isChecked
                    listener?.onItemCheckedChanged(node, binding.torrentFileSelected.isChecked)
                }
            }
        }

    }

}