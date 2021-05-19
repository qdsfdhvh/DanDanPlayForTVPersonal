package com.seiko.tv.ui.area

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import com.github.fragivity.navigator
import com.github.fragivity.push
import com.seiko.common.ui.adapter.AsyncObjectAdapter
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.data.model.api.BangumiSeason
import com.seiko.tv.ui.bangumi.BangumiDetailsFragment
import com.seiko.tv.ui.widget.presenter.BangumiPresenterSelector
import com.seiko.tv.ui.widget.presenter.SpacingVerticalGridPresenter
import com.seiko.tv.util.diff.HomeImageBeanDiffCallback
import com.seiko.tv.vm.BangumiAreaPageViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BangumiAreaPageFragment : VerticalGridSupportFragment(),
    BrowseSupportFragment.MainFragmentAdapterProvider, OnItemViewClickedListener {

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

    private val season by lazyAndroid {
        requireArguments().getParcelable<BangumiSeason>(ARGS_SEASON)!!
    }

    private val viewModel: BangumiAreaPageViewModel by viewModels()

    private lateinit var arrayAdapter: AsyncObjectAdapter<HomeImageBean>

    @Inject
    lateinit var presenterSelector: BangumiPresenterSelector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        getMainFragmentAdapter().fragmentHost.notifyViewCreated(mainFragmentAdapter)
    }

    private fun setupUI() {
        val verticalGridPresenter = SpacingVerticalGridPresenter(
            FocusHighlight.ZOOM_FACTOR_SMALL, false
        )
        verticalGridPresenter.numberOfColumns = COLUMNS
        verticalGridPresenter.setItemSpacing(40)

        onItemViewClickedListener = this
        gridPresenter = verticalGridPresenter

        arrayAdapter = AsyncObjectAdapter(presenterSelector, HomeImageBeanDiffCallback())
        adapter = arrayAdapter

    }

    private fun bindViewModel() {
        viewModel.bangumiList.observe(viewLifecycleOwner) { bangumiList ->
            arrayAdapter.submitList(bangumiList)
            getMainFragmentAdapter().fragmentHost.notifyDataReady(mainFragmentAdapter)
        }
        viewModel.season.value = season
    }

    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {
        when (item) {
            is HomeImageBean -> {
                navigator.push {
                    BangumiDetailsFragment.newInstance(item.animeId, item.imageUrl)
                }
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