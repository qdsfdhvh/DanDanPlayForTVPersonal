package com.seiko.tv.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.ui.presenter.BangumiPresenterSelector
import com.seiko.tv.util.diff.HomeImageBeanDiffCallback
import com.seiko.tv.vm.HomeViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class BangumiHistoryFragment : VerticalGridSupportFragment()
    , OnItemViewClickedListener {

    companion object {
        private const val COLUMNS = 7
    }

    private val viewModel: HomeViewModel by sharedViewModel()
    private lateinit var arrayAdapter: ArrayObjectAdapter
    private val diffCallback by lazy { HomeImageBeanDiffCallback() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupRowAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun setupUI() {
        title = "浏览历史"
    }

    private fun setupRowAdapter() {
        val verticalGridPresenter = VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM)
        verticalGridPresenter.numberOfColumns = COLUMNS
        onItemViewClickedListener = this
        gridPresenter = verticalGridPresenter

        val presenterSelector = BangumiPresenterSelector()
        arrayAdapter = ArrayObjectAdapter(presenterSelector)
        adapter = arrayAdapter
        prepareEntranceTransition()
    }

    private fun bindViewModel() {
        viewModel.historyBangumiList.observe(this::getLifecycle) { bangumiList ->
            arrayAdapter.setItems(bangumiList, diffCallback)
            startEntranceTransition()
        }
    }

    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {
        when(item) {
            is HomeImageBean -> {
                BangumiHistoryFragmentDirections.actionBangumiHistoryFragmentToBangumiDetailsFragment(
                    item.animeId
                )
            }
        }
    }


}