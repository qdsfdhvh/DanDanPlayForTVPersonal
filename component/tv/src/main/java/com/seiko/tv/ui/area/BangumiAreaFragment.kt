package com.seiko.tv.ui.area

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRowPresenter
import com.seiko.common.util.extensions.doOnIdle
import com.seiko.tv.R
import com.seiko.tv.data.model.SeasonPageRow
import com.seiko.tv.vm.BangumiAreaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BangumiAreaFragmentV2 : BrowseSupportFragment() {

    private val viewModel: BangumiAreaViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        bindViewModel()
    }

    private fun setupUI() {
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        title = getString(R.string.bangumi_area)
        brandColor = Color.parseColor("#424242")
        mainFragmentRegistry.registerFragment(SeasonPageRow::class.java, PageRowFragmentFactory())

        prepareEntranceTransition()
    }

    private fun bindViewModel() {
        viewModel.bangumiSeasons.observe(viewLifecycleOwner) { seasonList ->
            val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
            seasonList.mapIndexed { i, season ->
                val headerItem = HeaderItem(i.toLong(), season.seasonName)
                rowsAdapter.add(SeasonPageRow(headerItem, season))
            }
            adapter = rowsAdapter

            view?.doOnIdle {
                startEntranceTransition()
            }
        }
    }

}

private class PageRowFragmentFactory : BrowseSupportFragment.FragmentFactory<Fragment>() {

    override fun createFragment(row: Any?): Fragment {
        return when(row) {
            is SeasonPageRow -> BangumiAreaPageFragment.newInstance(row.season)
            else -> throw IllegalArgumentException(String.format("Invalid row %s", row))
        }
    }

}