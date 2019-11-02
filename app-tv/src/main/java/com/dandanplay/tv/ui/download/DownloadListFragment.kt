package com.dandanplay.tv.ui.download

import android.os.Bundle
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import androidx.leanback.widget.TitleViewAdapter.FULL_VIEW_VISIBLE
import com.dandanplay.tv.vm.DownloadListViewModel
import org.koin.android.ext.android.inject

class DownloadListFragment : VerticalGridSupportFragment(), OnItemViewClickedListener {

    private val viewModel by inject<DownloadListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupGridPresenter()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupUI()
    }

    private fun setupGridPresenter() {
        if (gridPresenter != null) return
        val presenter = VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_SMALL)
        presenter.numberOfColumns = 1
        gridPresenter = presenter
    }

    private fun setupUI() {
        title = "下载管理"
        showTitle(FULL_VIEW_VISIBLE)
        onItemViewClickedListener = this
    }

    override fun onItemClicked(holder: Presenter.ViewHolder?,
                               item: Any?,
                               rowHolder: RowPresenter.ViewHolder?,
                               row: Row?) {

    }

    companion object {
        const val TAG = "DownloadListFragment"
        fun newInstance() = DownloadListFragment()
    }
}