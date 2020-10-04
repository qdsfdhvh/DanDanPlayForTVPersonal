package com.seiko.tv.ui.bangumi

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import com.seiko.common.ui.adapter.AsyncObjectAdapter
import com.seiko.tv.R
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.ui.card.MainAreaCardView
import com.seiko.tv.ui.presenter.BangumiPresenterSelector
import com.seiko.tv.ui.presenter.SpacingVerticalGridPresenter
import com.seiko.tv.util.constants.MAX_BANGUMI_HISTORY_SIZE
import com.seiko.tv.util.diff.HomeImageBeanDiffCallback
import com.seiko.tv.vm.BangumiHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BangumiHistoryFragment : VerticalGridSupportFragment()
    , OnItemViewClickedListener {

    companion object {
        private const val COLUMNS = 5

        fun newInstance(): BangumiHistoryFragment {
            return BangumiHistoryFragment()
        }
    }

    private val viewModel: BangumiHistoryViewModel by viewModels()

    @Inject
    lateinit var presenterSelector: BangumiPresenterSelector

    private lateinit var arrayAdapter: AsyncObjectAdapter<HomeImageBean>

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
        verticalGridPresenter.setItemSpacing(40)

        onItemViewClickedListener = this
        gridPresenter = verticalGridPresenter

        arrayAdapter = AsyncObjectAdapter(presenterSelector, HomeImageBeanDiffCallback())
        adapter = arrayAdapter

        prepareEntranceTransition()
    }

    private fun bindViewModel() {
        viewModel.historyBangumiList.observe(viewLifecycleOwner) { bangumiList ->
            arrayAdapter.submitList(bangumiList)
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
                BangumiDetailsActivity.launch(requireActivity(), item, cardView.getImageView())
            }
        }
    }


}