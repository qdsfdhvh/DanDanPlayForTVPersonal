package com.dandanplay.tv.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.dandanplay.tv.R
import com.dandanplay.tv.databinding.FragmentAreaBinding
import com.dandanplay.tv.ui.adapter.BangumiRelateAdapter
import com.dandanplay.tv.ui.adapter.BangumiSeasonAdapter
import com.dandanplay.tv.ui.adapter.OnItemClickListener
import com.dandanplay.tv.ui.widget.SpaceItemDecoration
import com.dandanplay.tv.util.getPercentHeightSize
import com.dandanplay.tv.util.getPercentWidthSize
import com.dandanplay.tv.vm.BangumiAreaViewModel
import com.seiko.common.ResultData
import com.seiko.common.Status
import com.seiko.common.activity.addCallback
import com.seiko.common.activity.requireDispatchKeyEventDispatcher
import com.seiko.common.dialog.setLoadFragment
import com.seiko.common.extensions.lazyAndroid
import com.seiko.core.data.db.model.BangumiIntroEntity
import com.seiko.core.model.api.BangumiSeason
import org.koin.android.ext.android.inject
import java.lang.ref.WeakReference

class BangumiAreaFragment : Fragment(), OnItemClickListener {

    companion object {
//        private const val GRID_VIEW_LEFT_PX = 50 //80->60
//        private const val GRID_VIEW_RIGHT_PX = 50 //50->40
        private const val GRID_VIEW_TOP_PX = 50 //30->20
        private const val GRID_VIEW_BOTTOM_PX = 50 //50->40

        private const val ITEM_TOP_PADDING_PX = 25 //15->25
        private const val ITEM_RIGHT_PADDING_PX = 25
        
        private const val ARGS_SEASON_SELECTED_POSITION = "ARGS_SEASON_SELECTED_POSITION"
        private const val ARGS_BANGUMI_SELECTED_POSITION = "ARGS_BANGUMI_SELECTED_POSITION"
    }

    private val viewModel by inject<BangumiAreaViewModel>()

    private lateinit var binding: FragmentAreaBinding

    private lateinit var seasonAdapter: BangumiSeasonAdapter
    private lateinit var bangumiAdapter: BangumiRelateAdapter

    /**
     * 记录位置
     */
    private var seasonSelectedPosition: Int = -1
    private var bangumiSelectedPosition: Int = -1

    private val handler by lazyAndroid { AreaHandler(this) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAreaBinding.inflate(inflater, container, false)
        setupSeason()
        setupBangumi()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkSelectPosition(savedInstanceState)
        bindViewModel()
        registerKeyEvent()
    }

    /**
     * 请求季度合集 非强制
     */
    override fun onStart() {
        super.onStart()
        viewModel.getBangumiSeasons(false)
    }

    /**
     * 注销Item选择监听
     */
    override fun onDestroyView() {
        binding.gridSeason.removeOnChildViewHolderSelectedListener(mItemSelectedListener)
        binding.gridBangumi.removeOnChildViewHolderSelectedListener(mItemSelectedListener)
        super.onDestroyView()
    }

    /**
     * 保存视图状态
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        LogUtils.d("onSaveInstanceState")
        outState.putInt(ARGS_SEASON_SELECTED_POSITION, seasonSelectedPosition)
        outState.putInt(ARGS_BANGUMI_SELECTED_POSITION, bangumiSelectedPosition)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        LogUtils.d("onViewStateRestored")
    }

    private fun checkSelectPosition(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ARGS_SEASON_SELECTED_POSITION)) {
                seasonSelectedPosition = savedInstanceState.getInt(ARGS_SEASON_SELECTED_POSITION)
            }
            if (savedInstanceState.containsKey(ARGS_BANGUMI_SELECTED_POSITION)) {
                bangumiSelectedPosition = savedInstanceState.getInt(ARGS_BANGUMI_SELECTED_POSITION)
            }
        }
        if (seasonSelectedPosition != -1) {
            binding.gridSeason.selectedPosition = seasonSelectedPosition
            seasonAdapter.setSelectPosition(seasonSelectedPosition)
        }
        if (bangumiSelectedPosition != -1) {
            binding.gridBangumi.selectedPosition = bangumiSelectedPosition
            binding.gridBangumi.requestFocus()
        } else {
            binding.gridSeason.requestFocus()
        }
    }

    private fun setupSeason() {
        seasonAdapter = BangumiSeasonAdapter()
        seasonAdapter.setOnItemClickListener(this)

        binding.gridSeason.setNumColumns(1)
        binding.gridSeason.addOnChildViewHolderSelectedListener(mItemSelectedListener)
        binding.gridSeason.adapter = seasonAdapter
    }

    private fun setupBangumi() {
        bangumiAdapter = BangumiRelateAdapter()
        bangumiAdapter.setOnItemClickListener(this)
        // 自动计算count，由于用到了width，需要等界面绘制完，因此在post里运行
        binding.gridBangumi.post {
//            val top = getPercentHeightSize(ITEM_TOP_PADDING_PX)
//            val right = getPercentWidthSize(ITEM_RIGHT_PADDING_PX)
//            binding.gridBangumi.addItemDecoration(SpaceItemDecoration(top, right))
//            binding.gridBangumi.setPadding(GRID_VIEW_LEFT_PX, GRID_VIEW_TOP_PX, GRID_VIEW_RIGHT_PX, GRID_VIEW_BOTTOM_PX)

            // recView宽度，item宽度
            val width = binding.gridBangumi.width
            val itemWidth = requireContext().resources.getDimension(R.dimen.homeFragment_area_width).toInt()
            // 算出并排数、左右间距
            val count = width / (itemWidth + ITEM_RIGHT_PADDING_PX)
            val padding = width % (itemWidth + ITEM_RIGHT_PADDING_PX) / 2
            binding.gridBangumi.setPadding(padding, GRID_VIEW_TOP_PX, padding, GRID_VIEW_BOTTOM_PX)
            binding.gridBangumi.setNumColumns(count)
            binding.gridBangumi.addOnChildViewHolderSelectedListener(mItemSelectedListener)

            binding.gridBangumi.adapter = bangumiAdapter
        }
    }

    private fun bindViewModel() {
        viewModel.bangumiSeasons.observe(this::getLifecycle, this::updateSeasons)
        viewModel.bangumiList.observe(this::getLifecycle, this::updateBangumiList)
    }

    /**
     * 加载季度合集
     */
    private fun updateSeasons(data: ResultData<List<BangumiSeason>>) {
        when(data.responseType) {
            Status.LOADING -> {
                setLoadFragment(true)
            }
            Status.ERROR -> {
                setLoadFragment(false)
                LogUtils.w(data.error)
                ToastUtils.showShort(data.error.toString())
            }
            Status.SUCCESSFUL -> {
                setLoadFragment(false)
                val seasons = data.data ?: emptyList()

                seasonAdapter.items = seasons

                if (seasons.isNotEmpty()) {
                    var position = seasonSelectedPosition
                    if (position == -1 || position >= seasons.size) {
                        position = 0
                    }
                    LogUtils.d("Season Position = $position")
                    viewModel.getBangumiListWithSeason(seasons[position], false)
                }
            }
        }
    }

    /**
     * 加载动漫合集
     */
    private fun updateBangumiList(data: ResultData<List<BangumiIntroEntity>>) {
        when(data.responseType) {
            Status.LOADING -> {
                binding.progress.visibility = View.VISIBLE
            }
            Status.ERROR -> {
                binding.progress.visibility = View.GONE
                ToastUtils.showShort(data.error.toString())
            }
            Status.SUCCESSFUL -> {
                binding.progress.visibility = View.GONE
                bangumiAdapter.items = data.data ?: emptyList()
            }
        }
    }

    override fun onClick(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        when(item) {
            is BangumiIntroEntity -> {
                findNavController().navigate(
                    BangumiAreaFragmentDirections.actionBangumiAreaFragmentToBangumiDetailsFragment(
                        item.animeId
                    )
                )
            }
        }
    }

    /**
     * Item选择监听回调
     */
    private val mItemSelectedListener : OnChildViewHolderSelectedListener by lazyAndroid {
        object : OnChildViewHolderSelectedListener() {
            override fun onChildViewHolderSelected(
                parent: RecyclerView?,
                child: RecyclerView.ViewHolder?,
                position: Int,
                subposition: Int
            ) {
                when(parent) {
                    binding.gridSeason -> {
                        if (seasonSelectedPosition == position) return
                        seasonSelectedPosition = position
                        seasonAdapter.setSelectPosition(position)
                        // 请求动画
                        val item = seasonAdapter.get(position) ?: return
                        handler.send(item)
                    }
                    binding.gridBangumi -> {
                        if (bangumiSelectedPosition == position) return
                        bangumiSelectedPosition = position
                    }
                }
            }
        }
    }

    /**
     * 请求动漫数据，Handler用
     */
    fun getBangumiListWithSeason(item: BangumiSeason) {
        viewModel.getBangumiListWithSeason(item, true)
    }

    /**
     * 绑定按键监听到Activity
     */
    private fun registerKeyEvent() {
        requireDispatchKeyEventDispatcher().getDispatchKeyEventDispatcher()
            .addCallback(this, this::dispatchKeyEvent)
    }

    /**
     * 返回前，先把焦点给到左侧列表
     */
    private fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_BACK) {
            if (!binding.gridSeason.hasFocus()) {
                binding.gridSeason.requestFocus()
                return true
            }
        }
        return false
    }

}

/**
 * 防止左侧季度列表移动太快
 */
private class AreaHandler(fragment: BangumiAreaFragment) : Handler() {

    companion object {
        private const val HANDLER_WHAT_SEASON = 100
    }

    private val fragment = WeakReference(fragment)

    fun send(item: BangumiSeason) {
        removeMessages(HANDLER_WHAT_SEASON)
        val msg = obtainMessage(HANDLER_WHAT_SEASON)
        msg.obj = item
        sendMessageDelayed(msg, 500)
    }

    override fun handleMessage(msg: Message) {
        when(msg.what) {
            HANDLER_WHAT_SEASON -> {
                val item = msg.obj as? BangumiSeason ?: return
                fragment.get()?.getBangumiListWithSeason(item)
            }
        }
    }
}