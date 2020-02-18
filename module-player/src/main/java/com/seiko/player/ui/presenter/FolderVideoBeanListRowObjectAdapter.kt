package com.seiko.player.ui.presenter

import androidx.leanback.widget.*
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.ListUpdateCallback
import com.seiko.common.ui.adapter.AsyncObjectAdapter
import com.seiko.player.data.model.FolderVideoBean
import com.seiko.player.util.diff.FolderVideoBeanDiffCallback
import com.seiko.player.util.diff.VideoBeanDiffCallback

class FolderVideoBeanListRowObjectAdapter : ObjectAdapter(ListRowPresenter()) {

    private val presenterSelector = PlayerPresenterSelector()
    private val diffCallback = VideoBeanDiffCallback()

    private val listRowAdapterMap = HashMap<String, ListRow>()

    private val updateCallback = object : ListUpdateCallback {

        override fun onInserted(position: Int, count: Int) {
            notifyItemRangeInserted(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {

            notifyItemRangeRemoved(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            notifyItemRangeChanged(position, count, payload)
        }
    }

    override fun size(): Int {
        return differ.currentList.size
    }

    override fun get(position: Int): Any {
        val folderVideoBean = getFolderViewBean(position)
        val filePath = folderVideoBean.filePath
        var listRow = listRowAdapterMap[filePath]
        if (listRow == null) {
            val headerItem = HeaderItem(position.toLong(), filePath)
            val adapter = AsyncObjectAdapter(presenterSelector, diffCallback)
            adapter.submitList(folderVideoBean.childVideoList)
            listRow = ListRow(headerItem, adapter)
            listRowAdapterMap[filePath] = listRow
        }
        return listRow
    }

    private fun getFolderViewBean(position: Int): FolderVideoBean {
        return differ.currentList[position]
    }

    private val differ = AsyncListDiffer(updateCallback, AsyncDifferConfig
        .Builder<FolderVideoBean>(FolderVideoBeanDiffCallback())
        .build())

    private var maps = HashMap<String, ListRow>()

    fun submitList(list: List<FolderVideoBean>) {
        differ.submitList(list)
    }

}