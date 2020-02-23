package com.seiko.tv.ui.bangumi

import android.os.Bundle
import android.view.View
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import com.seiko.tv.R
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.ui.card.MainAreaCardView
import com.seiko.tv.ui.presenter.BangumiPresenterSelector
import com.seiko.tv.ui.presenter.SpacingVerticalGridPresenter
import com.seiko.tv.util.constants.MAX_BANGUMI_HISTORY_SIZE
import com.seiko.tv.util.diff.HomeImageBeanDiffCallback
import com.seiko.tv.vm.BangumiHistoryViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class BangumiHistoryFragment : VerticalGridSupportFragment()
    , OnItemViewClickedListener {

    companion object {
        private const val COLUMNS = 6

        fun newInstance(): BangumiHistoryFragment {
            return BangumiHistoryFragment()
        }
    }

    private val viewModel: BangumiHistoryViewModel by viewModel()

    private lateinit var arrayAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRowAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun setupRowAdapter() {
        val verticalGridPresenter = SpacingVerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM, false)
        verticalGridPresenter.numberOfColumns = COLUMNS
        verticalGridPresenter.setItemSpacing(25)

        onItemViewClickedListener = this
        gridPresenter = verticalGridPresenter

        val presenterSelector = BangumiPresenterSelector()
        arrayAdapter = ArrayObjectAdapter(presenterSelector)
        adapter = arrayAdapter

        prepareEntranceTransition()
    }

    private fun bindViewModel() {
        viewModel.historyBangumiList.observe(this::getLifecycle) { bangumiList ->
            arrayAdapter.setItems(bangumiList, HomeImageBeanDiffCallback())
            title = "%s (%s/%s)".format(getString(R.string.bangumi_history), bangumiList.size, MAX_BANGUMI_HISTORY_SIZE)
            startEntranceTransition()
        }
    }

    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {
        when(item) {
            is HomeImageBean -> {
                val cardView = itemViewHolder.view as MainAreaCardView
                BangumiDetailsActivity.launch(requireActivity(), item.animeId, cardView.getImageView())
            }
        }
    }


}