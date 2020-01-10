package com.dandanplay.tv.ui.bangumi

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.ToastUtils
import com.dandanplay.tv.ui.dialog.setLoadFragment
import com.dandanplay.tv.ui.presenter.MainAreaPresenter
import com.dandanplay.tv.vm.BangumiTimeLineViewModel
import com.seiko.common.ResultData
import com.seiko.common.Status
import com.seiko.data.model.api.AirDayBangumiBean
import com.seiko.data.model.api.BangumiIntro
import org.koin.android.viewmodel.ext.android.viewModel

class BangumiTimeLineFragment : BrowseSupportFragment(), OnItemViewClickedListener {

    private val viewModel by viewModel<BangumiTimeLineViewModel>()

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
        viewModel.airDayBangumiList.observe(this::getLifecycle, this::updateUI)
        if (viewModel.airDayBangumiList.value == null) {
            viewModel.getBangumiList()
        }
    }

    private fun updateUI(data: ResultData<List<AirDayBangumiBean>>) {
        when (data.responseType) {
            Status.LOADING -> {
                setLoadFragment(true)
            }
            Status.ERROR -> {
                setLoadFragment(false)
                ToastUtils.showShort(data.error.toString())
            }
            Status.SUCCESSFUL -> {
                setLoadFragment(false)
                updateAirDayBangumiList(data.data ?: return)
            }
        }
    }

    private fun updateAirDayBangumiList(list: List<AirDayBangumiBean>) {
        var areaAdapter: ArrayObjectAdapter
        var headerItem: HeaderItem
        val rows = list.map { airDay ->
            areaAdapter = ArrayObjectAdapter(MainAreaPresenter())
            headerItem = HeaderItem(airDay.id.toLong(), getWeekName(airDay.id))
            areaAdapter.addAll(0, airDay.bangumiList)
            ListRow(headerItem, areaAdapter)
        }
        if (rowsAdapter.size() > 0) rowsAdapter.clear()
        rowsAdapter.addAll(0, rows)
    }

    override fun onItemClicked(holder: Presenter.ViewHolder?, item: Any?,
                               rowHolder: RowPresenter.ViewHolder?, row: Row?) {
        when(item) {
            is BangumiIntro -> {
                findNavController().navigate(
                    BangumiTimeLineFragmentDirections.actionBangumiTimeLineFragmentToBangumiDetailsFragment(item.animeId)
                )
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