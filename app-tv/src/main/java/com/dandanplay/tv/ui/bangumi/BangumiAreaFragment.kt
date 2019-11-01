package com.dandanplay.tv.ui.bangumi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlightHelper
import androidx.leanback.widget.ItemBridgeAdapter
import androidx.leanback.widget.OnChildViewHolderSelectedListener
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.dandanplay.tv.R
import com.dandanplay.tv.ui.dialog.setLoadFragment
import com.dandanplay.tv.ui.presenter.BangumiRelatedPresenter
import com.dandanplay.tv.ui.presenter.BangumiSeasonPresenter
import com.dandanplay.tv.vm.BangumiAreaViewModel
import com.seiko.common.ResultData
import com.seiko.common.Status
import com.seiko.domain.entity.BangumiIntro
import com.seiko.domain.entity.BangumiSeason
import kotlinx.android.synthetic.main.fragment_area.*
import org.koin.android.ext.android.inject

class BangumiAreaFragment : Fragment() {

    private val viewModel by inject<BangumiAreaViewModel>()

    private val seasonAdapter = ArrayObjectAdapter(BangumiSeasonPresenter())
    private val bangumiAdapter = ArrayObjectAdapter(BangumiRelatedPresenter())

    private val childViewHolderSelectedListener = object : OnChildViewHolderSelectedListener() {
        override fun onChildViewHolderSelected(
            parent: RecyclerView?,
            child: RecyclerView.ViewHolder?,
            position: Int,
            subposition: Int) {
            if (position < 0) return

            val item = seasonAdapter.get(position)
            when(item) {
                is BangumiSeason -> {
                    viewModel.getBangumiListWithSeason(item)
                }
            }
        }

        override fun onChildViewHolderSelectedAndPositioned(
            parent: RecyclerView?,
            child: RecyclerView.ViewHolder?,
            position: Int,
            subposition: Int) {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_area, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        loadData()
    }

    private fun setupUI() {
        grid_season.setNumColumns(1)
        grid_season.setOnChildViewHolderSelectedListener(childViewHolderSelectedListener)
        var bridgeAdapter = ItemBridgeAdapter(seasonAdapter)
        grid_season.setItemSpacing(20)
        grid_season.adapter = bridgeAdapter
        grid_season.requestFocus()
        FocusHighlightHelper.setupHeaderItemFocusHighlight(bridgeAdapter, true)

        grid_bangumi.setNumColumns(4)
        bridgeAdapter = ItemBridgeAdapter(bangumiAdapter)
        grid_bangumi.setItemSpacing(40)
//        grid_bangumi.verticalSpacing = 10
//        grid_bangumi.horizontalSpacing = 40
//        grid_bangumi.verticalSpacing = 40
        grid_bangumi.adapter = bridgeAdapter
        FocusHighlightHelper.setupHeaderItemFocusHighlight(bridgeAdapter, true)
    }

    private fun loadData() {
        viewModel.bangumiSeasons.observe(this::getLifecycle, this::updateSeasons)
        viewModel.bangumiList.observe(this::getLifecycle, this::updateBangumiList)
        if (viewModel.bangumiSeasons.value == null) {
            viewModel.getBangumiSeasons()
        }
    }

    private fun updateSeasons(data: ResultData<List<BangumiSeason>>) {
        when(data.responseType) {
            Status.LOADING -> {
                setLoadFragment(true)
            }
            Status.ERROR -> {
                setLoadFragment(false)
                ToastUtils.showShort(data.error.toString())
            }
            Status.SUCCESSFUL -> {
                setLoadFragment(false)
                seasonAdapter.clear()
                val seasons = data.data ?: return
                seasonAdapter.addAll(0, seasons)
                if (seasons.isNotEmpty()) {
                    viewModel.getBangumiListWithSeason(seasons[0])
                }
            }
        }
    }

    private fun updateBangumiList(data: ResultData<List<BangumiIntro>>) {
        when(data.responseType) {
            Status.LOADING -> {
                setLoadFragment(true)
            }
            Status.ERROR -> {
                setLoadFragment(false)
                ToastUtils.showShort(data.error.toString())
            }
            Status.SUCCESSFUL -> {
                setLoadFragment(false)
                if (bangumiAdapter.size() > 0) bangumiAdapter.clear()
                bangumiAdapter.addAll(0, data.data)
            }
        }
    }
}