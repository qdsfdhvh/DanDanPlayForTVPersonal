package com.seiko.tv.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.findNavController
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.data.model.api.BangumiSeason
import com.seiko.tv.ui.presenter.BangumiPresenterSelector
import com.seiko.tv.ui.presenter.SpacingVerticalGridPresenter
import com.seiko.tv.util.diff.HomeImageBeanDiffCallback
import com.seiko.tv.vm.BangumiAreaPageViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class BangumiAreaPageFragment : GridFragment()
    , OnItemViewClickedListener {

    companion object {
        private const val COLUMNS = 6
        private const val ARGS_SEASON = "ARGS_SEASON"

        fun newInstance(season: BangumiSeason): BangumiAreaPageFragment {
            val bundle = Bundle()
            bundle.putParcelable(ARGS_SEASON, season)
            val fragment = BangumiAreaPageFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private val season by lazyAndroid { arguments!!.getParcelable<BangumiSeason>(ARGS_SEASON)!! }

    private val viewModel: BangumiAreaPageViewModel by viewModel()

    private lateinit var arrayAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun setupUI() {
        val verticalGridPresenter = SpacingVerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM, false)
        verticalGridPresenter.numberOfColumns = COLUMNS
        verticalGridPresenter.setItemSpacing(25)

        onItemViewClickedListener = this
        gridPresenter = verticalGridPresenter

        val presenterSelector = BangumiPresenterSelector()
        arrayAdapter = ArrayObjectAdapter(presenterSelector)
        adapter = arrayAdapter
    }

    private fun bindViewModel() {
        viewModel.bangumiList.observe(this::getLifecycle) { bangumiList ->
            arrayAdapter.setItems(bangumiList, HomeImageBeanDiffCallback())
        }
        viewModel.season.value = season
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
                    BangumiAreaFragmentV2Directions.actionBangumiAreaFragmentV2ToBangumiDetailsFragment(
                        item.animeId
                    )
                )
            }
        }
    }
}