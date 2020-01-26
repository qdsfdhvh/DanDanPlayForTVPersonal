/*
 * Copyright (C) 2016 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of LibreTorrent.
 *
 * LibreTorrent is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LibreTorrent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LibreTorrent.  If not, see <http://www.gnu.org/licenses/>.
 */
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

/*
 * The adapter for representation of downloadable files in a file tree view.
 */
class DownloadableFilesAdapter : RecyclerView.Adapter<DownloadableFilesAdapter.ViewHolder>() {

    private var files: MutableList<BencodeFileTree> = ArrayList()

    fun setFiles(list: Collection<BencodeFileTree>) {
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
                R.drawable.ic_folder_grey600_24dp
            } else {
                R.drawable.ic_file_grey600_24dp
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

            val file = files[position]
            if (file.type == FileNode.Type.FILE) {
                /* Check file if it clicked */
                binding.torrentFileSelected.isChecked = !binding.torrentFileSelected.isChecked
                listener?.onItemCheckedChanged(file, binding.torrentFileSelected.isChecked)
            } else {
                listener?.onItemClicked(file)
            }
        }

    }

}