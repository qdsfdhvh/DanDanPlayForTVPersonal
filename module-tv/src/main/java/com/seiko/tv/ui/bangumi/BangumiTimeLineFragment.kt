package com.seiko.tv.ui.bangumi

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import com.seiko.tv.vm.HomeViewModel
import com.seiko.tv.data.model.AirDayBangumiBean
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.ui.card.MainAreaCardView
import com.seiko.tv.ui.presenter.BangumiPresenterSelector
import org.koin.android.viewmodel.ext.android.viewModel

class BangumiTimeLineFragment : BrowseSupportFragment(), OnItemViewClickedListener {

    companion object {
        fun newInstance(): BangumiTimeLineFragment {
            return BangumiTimeLineFragment()
        }
    }

    private val viewModel by viewModel<HomeViewModel>()

    private lateinit var rowsAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun setupUI() {
        title = "放送表"
        brandColor = Color.parseColor("#424242")

        if (adapter != null) return
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        adapter = rowsAdapter
        onItemViewClickedListener = this
    }

    private fun bindViewModel() {
        viewModel.weekBangumiList.observe(this::getLifecycle) { bangumiList ->
            updateAirDayBangumiList(bangumiList)
        }
//        viewModel.getBangumiList(false)
    }

    private fun updateAirDayBangumiList(list: List<AirDayBangumiBean>) {
        var areaAdapter: ArrayObjectAdapter
        var headerItem: HeaderItem
        val rows = list.map { airDay ->
            val presenterSelector = BangumiPresenterSelector()
            areaAdapter = ArrayObjectAdapter(presenterSelector)
            headerItem = HeaderItem(airDay.id.toLong(), getWeekName(airDay.id))
            areaAdapter.addAll(0, airDay.bangumiList)
            ListRow(headerItem, areaAdapter)
        }
        if (rowsAdapter.size() > 0) rowsAdapter.clear()
        rowsAdapter.addAll(0, rows)
    }

    override fun onItemClicked(holder: Presenter.ViewHolder, item: Any?,
                               rowHolder: RowPresenter.ViewHolder?, row: Row?) {
        when(item) {
            is HomeImageBean -> {
                val cardView = holder.view as MainAreaCardView
                BangumiDetailsActivity.launch(requireActivity(), item.animeId, cardView.getImageView())
            }
        }
    }
}

private fun getWeekName(id: Int) = when(id) {
    0 -> "周日"
    1 -> "周一"
    2 -> "周二"
    3 -> "周三"
    4 -> "周四"
    5 -> "周五"
    6 -> "周六"
    else -> ""
}