package com.seiko.player.ui.media

import android.os.Bundle
import android.view.View
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.*
import com.seiko.common.ui.adapter.AsyncObjectAdapter
import com.seiko.player.data.db.model.VideoMedia
import com.seiko.player.data.model.PlayParam
import com.seiko.player.data.model.VideoBean
import com.seiko.player.ui.presenter.FolderVideoBeanListRowObjectAdapter
import com.seiko.player.ui.presenter.PlayerPresenterSelector
import com.seiko.player.ui.video.VideoPlayerActivity
import com.seiko.player.util.diff.VideoBeanDiffCallback
import com.seiko.player.vm.MediaViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class MediaFragment : RowsSupportFragment() {

    private val viewModel: MediaViewModel by viewModel()

    private lateinit var rowsAdapter: ArrayObjectAdapter
    private lateinit var mediaAdapter: ArrayObjectAdapter

    private val mItemClickListener =
        BaseOnItemViewClickedListener<Row> { itemViewHolder, item, rowViewHolder, row ->
            if (item is VideoBean) {
                VideoPlayerActivity.launch(requireActivity(), PlayParam(
                    videoPath = item.videoPath,
                    videoTitle = item.videoTitle
                ))
            }
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
        val headerItem = HeaderItem(0, "媒体库")
        mediaAdapter = ArrayObjectAdapter(presenterSelector)
        rowsAdapter.add(ListRow(headerItem, mediaAdapter))
        adapter = rowsAdapter
    }

    private fun bindViewModel() {
        viewModel.videoList.observe(this::getLifecycle) { list ->
            mediaAdapter.setItems(list, VideoBeanDiffCallback())
        }
    }

}