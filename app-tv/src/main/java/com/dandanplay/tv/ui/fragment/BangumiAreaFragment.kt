package com.dandanplay.tv.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.leanback.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.dandanplay.tv.R
import com.dandanplay.tv.databinding.FragmentAreaBinding
import com.dandanplay.tv.ui.adapter.BangumiRelateAdapter
import com.dandanplay.tv.ui.presenter.BangumiSeasonPresenter
import com.dandanplay.tv.ui.widget.SpaceItemDecoration
import com.dandanplay.tv.util.getPercentHeightSize
import com.dandanplay.tv.util.getPercentWidthSize
import com.dandanplay.tv.vm.BangumiAreaViewModel
import com.seiko.common.ResultData
import com.seiko.common.Status
import com.seiko.common.dialog.setLoadFragment
import com.seiko.core.data.db.model.BangumiIntroEntity
import com.seiko.core.model.api.BangumiSeason
import me.jessyan.autosize.AutoSizeConfig
import org.koin.android.ext.android.inject

class BangumiAreaFragment : Fragment() {

    companion object {
        private const val GRID_VIEW_LEFT_PX = 80 //80->60
        private const val GRID_VIEW_RIGHT_PX = 50 //50->40
        private const val GRID_VIEW_TOP_PX = 30 //30->20
        private const val GRID_VIEW_BOTTOM_PX = 50 //50->40

        private const val ITEM_TOP_PADDING_PX = 25 //15->25
        private const val ITEM_RIGHT_PADDING_PX = 25
    }

    private val viewModel by inject<BangumiAreaViewModel>()

    private lateinit var binding: FragmentAreaBinding
//    private val seasonAdapter = ArrayObjectAdapter(BangumiSeasonPresenter())
//    private val bangumiAdapter = ArrayObjectAdapter(BangumiRelatedPresenter())
    private lateinit var seasonAdapter: ArrayObjectAdapter
    private lateinit var bangumiAdapter: BangumiRelateAdapter

//    private var listPosition = 0

    private val childViewHolderSelectedListener = object : OnChildViewHolderSelectedListener() {
        override fun onChildViewHolderSelected(
            parent: RecyclerView?,
            child: RecyclerView.ViewHolder?,
            position: Int,
            subposition: Int
        ) {
//            if (position < 0) return
//
//            when(parent?.id) {
//                R.id.grid_season -> {
//                    val seasons = seasonAdapter.get(position) as? BangumiSeason ?: return
//                    viewModel.getBangumiListWithSeason(seasons)
//                }
////                R.id.binding.gridBangumi -> {
////                    val item = bangumiAdapter.get(position) ?: return
////                    BangumiAreaFragmentDirections.actionBangumiAreaFragmentToBangumiDetailsFragment(
////                        item.animeId
////                    )
////                }
//            }
        }

        override fun onChildViewHolderSelectedAndPositioned(
            parent: RecyclerView?,
            child: RecyclerView.ViewHolder?,
            position: Int,
            subposition: Int
        ) {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAreaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        loadData()
    }

    private fun setupUI() {
        setupSeason()
        setupBangumi()
    }

    private fun setupSeason() {
//        var bridgeAdapter = ItemBridgeAdapter(seasonAdapter)
//        grid_season.setNumColumns(1)
//        grid_season.setOnChildViewHolderSelectedListener(childViewHolderSelectedListener)
//        grid_season.setItemSpacing(20)
//        grid_season.adapter = bridgeAdapter
//        grid_season.requestFocus()
//        FocusHighlightHelper.setupHeaderItemFocusHighlight(bridgeAdapter, true)

        seasonAdapter = ArrayObjectAdapter(BangumiSeasonPresenter())
        val itemBridgeAdapter = ItemBridgeAdapter(seasonAdapter)
        binding.gridSeason.setNumColumns(1)
        binding.gridSeason.adapter = itemBridgeAdapter
        binding.gridSeason.setOnChildViewHolderSelectedListener(childViewHolderSelectedListener)
        binding.gridSeason.requestFocus()
        FocusHighlightHelper.setupHeaderItemFocusHighlight(itemBridgeAdapter, true)

    }

    private fun setupBangumi() {
//        bridgeAdapter = ItemBridgeAdapter(bangumiAdapter)
//        binding.gridBangumi.setNumColumns(4)
//        binding.gridBangumi.setItemSpacing(40)
//        binding.gridBangumi.adapter = bridgeAdapter
//        FocusHighlightHelper.setupHeaderItemFocusHighlight(bridgeAdapter, true)

        bangumiAdapter = BangumiRelateAdapter()
        val top = getPercentHeightSize(ITEM_TOP_PADDING_PX)
        val right = getPercentWidthSize(ITEM_RIGHT_PADDING_PX)
        binding.gridBangumi.addItemDecoration(SpaceItemDecoration(top, right))
        binding.gridBangumi.setPadding(GRID_VIEW_LEFT_PX, GRID_VIEW_TOP_PX, GRID_VIEW_RIGHT_PX, GRID_VIEW_BOTTOM_PX)
        binding.gridBangumi.post {
            LogUtils.d("GridWidth = ${binding.gridBangumi.width}")

            val itemWidth = requireContext().resources.getDimension(R.dimen.homeFragment_area_width)
            val itemHeight = requireContext().resources.getDimension(R.dimen.homeFragment_area_height)

            val count = ((binding.gridBangumi.width - GRID_VIEW_LEFT_PX - GRID_VIEW_RIGHT_PX) / (itemWidth + ITEM_RIGHT_PADDING_PX)).toInt()
            LogUtils.i("count = $count")

            binding.gridBangumi.setNumColumns(count)

            LogUtils.i("width=${AutoSizeConfig.getInstance().designWidthInDp}, height=${AutoSizeConfig.getInstance().designHeightInDp}")
            LogUtils.i("itemWidth=$itemWidth, itemHeight=$itemHeight")
            binding.gridBangumi.adapter = bangumiAdapter
        }
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

    private fun updateBangumiList(data: ResultData<List<BangumiIntroEntity>>) {
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
                bangumiAdapter.items = data.data ?: emptyList()
            }
        }
    }
}