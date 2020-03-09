package com.seiko.tv.ui.area

import android.os.Bundle
import android.view.View
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.observe
import com.seiko.common.ui.adapter.AsyncObjectAdapter
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.data.model.api.BangumiSeason
import com.seiko.tv.ui.bangumi.BangumiDetailsActivity
import com.seiko.tv.ui.card.MainAreaCardView
import com.seiko.tv.ui.presenter.BangumiPresenterSelector
import com.seiko.tv.ui.presenter.SpacingVerticalGridPresenter
import com.seiko.tv.util.diff.HomeImageBeanDiffCallback
import com.seiko.tv.vm.BangumiAreaPageViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class BangumiAreaPageFragment : VerticalGridSupportFragment()
    , BrowseSupportFragment.MainFragmentAdapterProvider
    , OnItemViewClickedListener {

    companion object {
        private const val COLUMNS = 5
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

    private lateinit var arrayAdapter: AsyncObjectAdapter<HomeImageBean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        getMainFragmentAdapter().fragmentHost.notifyViewCreated(mainFragmentAdapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unBindViewModel()
    }

    private fun setupUI() {
        val verticalGridPresenter = SpacingVerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_SMALL, false)
        verticalGridPresenter.numberOfColumns = COLUMNS
        verticalGridPresenter.setItemSpacing(40)

        onItemViewClickedListener = this
        gridPresenter = verticalGridPresenter

        val presenterSelector = BangumiPresenterSelector()
        arrayAdapter = AsyncObjectAdapter(presenterSelector, HomeImageBeanDiffCallback())
        adapter = arrayAdapter

    }

    private fun bindViewModel() {
        viewModel.bangumiList.observe(this) { bangumiList ->
            arrayAdapter.submitList(bangumiList)

            getMainFragmentAdapter().fragmentHost.notifyDataReady(mainFragmentAdapter)
        }
        viewModel.season.value = season
    }

    private fun unBindViewModel() {
        viewModel.bangumiList.removeObservers(this)
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

    override fun showTitle(show: Boolean) {
        super.showTitle(show)
        mainFragmentAdapter.fragmentHost.showTitleView(show)
    }

    private val mainFragmentAdapter = BrowseSupportFragment.MainFragmentAdapter(this)

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<*> {
        return mainFragmentAdapter
    }

}