package com.seiko.tv.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.leanback.app.FixBrowseSupportFragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.findNavController
import com.seiko.tv.R
import com.seiko.tv.data.model.HomeSettingBean
import com.seiko.common.ui.dialog.DialogSelectFragment
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.util.diff.HomeImageBeanDiffCallback
import com.seiko.tv.vm.HomeViewModel
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.common.router.Navigator
import com.seiko.common.util.toast.toast
import com.seiko.tv.ui.presenter.BangumiPresenterSelector
import org.koin.android.viewmodel.ext.android.viewModel

class HomeFragment : FixBrowseSupportFragment()
    , OnItemViewClickedListener
    , View.OnClickListener {

    companion object {
        private const val ROW_AREA = 0
        private const val ROW_FAVORITE = 1
        private const val ROW_SETTING = 2
        private const val ROW_HISTORY = 3

        private const val ID_AREA = 0
        private const val ID_FAVOURITE = 1
        private const val ID_TIME = 2
        private const val ID_INDEX = 3
        private const val ID_SETTING = 4
        private const val ID_DOWNLOAD = 5
        private const val ID_HISTORY = 6
    }

    private val viewModel by viewModel<HomeViewModel>()

    private lateinit var rowsAdapter: ArrayObjectAdapter
    private lateinit var arrayAdapters: SparseArray<ArrayObjectAdapter>

    private val leftItems by lazyAndroid {
        listOf(
            HomeSettingBean(ID_AREA, getString(R.string.bangumi_area), R.drawable.ic_bangumi_area),
//            MyBean(ID_FAVOURITE, "追 番", R.drawable.ic_bangumi_favourite),
            HomeSettingBean(ID_TIME, getString(R.string.bangumi_time), R.drawable.ic_bangumi_time),
//            MyBean(ID_INDEX, "索引", R.drawable.ic_bangumi_index)
            HomeSettingBean(ID_HISTORY, getString(R.string.bangumi_history), R.drawable.ic_bangumi_history),
            HomeSettingBean(ID_DOWNLOAD, getString(R.string.bangumi_download), R.drawable.ic_bangumi_download),
            HomeSettingBean(ID_SETTING, getString(R.string.bangumi_setting), R.drawable.ic_bangumi_setting)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupRows()
        bindViewModel()
    }

    /**
     * 生成相关UI
     */
    private fun setupUI() {
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        title = getString(R.string.app_name)
        brandColor = Color.parseColor("#424242")

        // 设置搜索键
        searchAffordanceColor = ContextCompat.getColor(requireActivity(), R.color.colorAccent)
        setOnSearchClickedListener(this)

        // 监听返回键
        requireActivity().onBackPressedDispatcher.addCallback(this) { onBackPressed() }
    }

    /**
     * 生成Rows
     */
    private fun setupRows() {
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        arrayAdapters = SparseArray(3)
        // 今日更新
        createListRow(ROW_AREA, getString(R.string.title_area))
        // 我的收藏
        createListRow(ROW_FAVORITE, getString(R.string.title_favorite))
        // 浏览历史
        createListRow(ROW_HISTORY, getString(R.string.title_history))
        // 工具中心
        createListRow(ROW_SETTING, getString(R.string.title_setting))
        // 绑定Adapter
        adapter = rowsAdapter
        onItemViewClickedListener = this
    }

    private fun createListRow(id: Int, title: String) {
        val presenterSelector = BangumiPresenterSelector()
        val headerItem = HeaderItem(id.toLong(), title)
        val objectAdapter = ArrayObjectAdapter(presenterSelector)
        val listRow = ListRow(headerItem, objectAdapter)
        rowsAdapter.add(listRow)
        arrayAdapters.put(id, objectAdapter)
    }

    /**
     * 开始加载数据
     */
    private fun bindViewModel() {
        viewModel.todayBangumiList.observe(this::getLifecycle) { bangumiList ->
            arrayAdapters.get(ROW_AREA)?.setItems(bangumiList, HomeImageBeanDiffCallback())
        }
        viewModel.favoriteBangumiList.observe(this::getLifecycle) { favoriteList ->
            arrayAdapters.get(ROW_FAVORITE)?.setItems(favoriteList, HomeImageBeanDiffCallback())
        }
        viewModel.historyBangumiList.observe(this::getLifecycle) { historyList ->
            arrayAdapters.get(ROW_HISTORY)?.setItems(historyList, HomeImageBeanDiffCallback())
        }
        // 加载个人数据
        arrayAdapters.get(ROW_SETTING)?.setItems(leftItems, null)
    }

    /**
     * PS: 控件id查看 {@link [androidx.leanback.widget.TitleView]}
     */
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.title_orb -> {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToSearchBangumiFragment()
                )
            }
        }
    }

    /**
     * 点击：新番、个人中心
     */
    override fun onItemClicked(holder: Presenter.ViewHolder, item: Any?,
                               rowHolder: RowPresenter.ViewHolder?, row: Row?) {
        when(item) {
            is HomeImageBean -> {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToBangumiDetailsFragment(
                        item.animeId
                    )
                )
            }
            is HomeSettingBean -> {
                when(item.id) {
                    ID_AREA -> {
//                        findNavController().navigate(
//                            HomeFragmentDirections.actionHomeFragmentToBangumiAreaFragment()
//                        )
                        findNavController().navigate(
                            HomeFragmentDirections.actionHomeFragmentToBangumiAreaFragmentV2()
                        )
                    }
                    ID_TIME -> {
                        findNavController().navigate(
                            HomeFragmentDirections.actionHomeFragmentToBangumiTimeLineFragment()
                        )
                    }
                    ID_HISTORY -> {
                        findNavController().navigate(
                            HomeFragmentDirections.actionHomeFragmentToBangumiHistoryFragment()
                        )
                    }
                    ID_DOWNLOAD -> {
                        Navigator.navToTorrent(requireActivity())
                    }
                    ID_SETTING -> {
                        toast("待施工")
                    }
                }
            }
        }
    }

    /**
     * 退出
     */
    private fun onBackPressed() {
        if (childFragmentManager.findFragmentByTag(DialogSelectFragment.TAG) == null) {
            DialogSelectFragment.Builder()
                .setTitle(getString(R.string.msg_exit_app))
                .setConfirmText(getString(R.string.exit))
                .setCancelText(getString(R.string.cancel))
                .setConfirmClickListener {
                    ActivityCompat.finishAffinity(requireActivity())
                }
                .build()
                .show(childFragmentManager)
        }
    }
}