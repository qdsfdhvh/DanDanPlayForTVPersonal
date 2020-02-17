package com.seiko.player.ui.media

import android.os.Bundle
import android.view.View
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.*
import com.seiko.common.ui.adapter.AsyncObjectAdapter
import com.seiko.player.data.db.model.VideoMedia
import com.seiko.player.ui.presenter.PlayerPresenterSelector
import com.seiko.player.util.diff.VideoMediaDiffCallback
import com.seiko.player.vm.MediaViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class MediaFragment : RowsSupportFragment() {

    private val viewModel: MediaViewModel by viewModel()

    private lateinit var rowsAdapter: ArrayObjectAdapter
    private lateinit var mediaAdapter: AsyncObjectAdapter<VideoMedia>

    private val mItemClickListener =
        BaseOnItemViewClickedListener<Row> { itemViewHolder, item, rowViewHolder, row ->

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupRows()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun setupUI() {
        onItemViewClickedListener = mItemClickListener

    }

    private fun setupRows() {
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val presenterSelector = PlayerPresenterSelector()
        mediaAdapter = AsyncObjectAdapter(presenterSelector, VideoMediaDiffCallback())
        createListRow(0, "媒体库", mediaAdapter)

        adapter = rowsAdapter
    }

    private fun bindViewModel() {
        viewModel.mediaList.observe(this::getLifecycle) { mediaList ->
            mediaAdapter.submitList(mediaList)
        }
    }

    private fun  createListRow(id: Int, title: String, objectAdapter: ObjectAdapter) {
        val headerItem = HeaderItem(id.toLong(), title)
        val listRow = ListRow(headerItem, objectAdapter)
        rowsAdapter.add(listRow)
    }
}