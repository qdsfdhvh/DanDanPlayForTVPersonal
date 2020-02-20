package com.seiko.tv.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.findNavController
import com.seiko.tv.R
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.ui.presenter.BangumiPresenterSelector
import com.seiko.tv.util.constants.MAX_BANGUMI_HISTORY_SIZE
import com.seiko.tv.util.diff.HomeImageBeanDiffCallback
import com.seiko.tv.vm.BangumiHistoryViewModel
import com.seiko.tv.vm.HomeViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class BangumiHistoryFragment : VerticalGridSupportFragment()
    , OnItemViewClickedListener {

    companion object {
        private const val COLUMNS = 7
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
        val verticalGridPresenter = VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM, false)
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
            arrayAdapter.setItems(bangumiList, HomeImageBeanDiffCallback())
            title = "%s (%s/%s)".format(getString(R.string.bangumi_history),
                bangumiList.size, MAX_BANGUMI_HISTORY_SIZE)
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
                findNavController().navigate(
                    BangumiHistoryFragmentDirections.actionBangumiHistoryFragmentToBangumiDetailsFragment(
                        item.animeId
                    )
                )
            }
        }
    }


}