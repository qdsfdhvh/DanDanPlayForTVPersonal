package com.seiko.tv.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
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
import com.seiko.common.ui.adapter.AsyncObjectAdapter
import com.seiko.common.ui.adapter.AsyncPagedObjectAdapter
import com.seiko.common.util.toast.toast
import com.seiko.tv.ui.presenter.BangumiPresenterSelector
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class HomeFragment : BrowseSupportFragment()
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
//        private const val ID_INDEX = 3
        private const val ID_SETTING = 4
        private const val ID_DOWNLOAD = 5
        private const val ID_HISTORY = 6
        private const val ID_MEDIA = 7
    }

    private val viewModel by viewModel<HomeViewModel>()

    private lateinit var rowsAdapter: ArrayObjectAdapter
    private lateinit var settingAdapter: ArrayObjectAdapter
    private lateinit var areaAdapter: AsyncObjectAdapter<HomeImageBean>
    private lateinit var favoriteAdapter: AsyncPagedObjectAdapter<HomeImageBean>
    private lateinit var historyAdapter: AsyncPagedObjectAdapter<HomeImageBean>

    private val leftItems by lazyAndroid {
        listOf(
            HomeSettingBean(ID_AREA, getString(R.string.bangumi_area), R.drawable.ic_bangumi_area),
            HomeSettingBean(ID_TIME, getString(R.string.bangumi_time), R.drawable.ic_bangumi_time),
            HomeSettingBean(ID_FAVOURITE, getString(R.string.bangumi_favorite), R.drawable.ic_bangumi_favourite),
            HomeSettingBean(ID_HISTORY, getString(R.string.bangumi_history), R.drawable.ic_bangumi_history),
            HomeSettingBean(ID_MEDIA, getString(R.string.bangumi_media), R.drawable.ic_bangumi_media),
            HomeSettingBean(ID_DOWNLOAD, getString(R.string.bangumi_download), R.drawable.ic_bangumi_download),
            HomeSettingBean(ID_SETTING, getString(R.string.bangumi_setting), R.drawable.ic_bangumi_setting)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupRows()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        val presenterSelector = BangumiPresenterSelector()
        val homeImageBeanDiffCallback = HomeImageBeanDiffCallback()
        // 工具中心
        settingAdapter = ArrayObjectAdapter(presenterSelector)
        settingAdapter.setItems(leftItems, null)
        createListRow(ROW_SETTING, getString(R.string.title_setting), settingAdapter)
        // 今日更新
        areaAdapter = AsyncObjectAdapter(presenterSelector, homeImageBeanDiffCallback)
        createListRow(ROW_AREA, getString(R.string.title_area), areaAdapter)
        // 我的收藏
        favoriteAdapter = AsyncPagedObjectAdapter(presenterSelector, homeImageBeanDiffCallback)
        createListRow(ROW_FAVORITE, getString(R.string.title_favorite), favoriteAdapter)
        // 浏览历史
        historyAdapter = AsyncPagedObjectAdapter(presenterSelector, homeImageBeanDiffCallback)
        createListRow(ROW_HISTORY, getString(R.string.title_history), historyAdapter)
        // 绑定Adapter
        adapter = rowsAdapter
        onItemViewClickedListener = this
    }

    private fun  createListRow(id: Int, title: String, objectAdapter: ObjectAdapter) {
        val headerItem = HeaderItem(id.toLong(), title)
        val listRow = ListRow(headerItem, objectAdapter)
        rowsAdapter.add(listRow)
    }

    /**
     * 开始加载数据
     */
    private fun bindViewModel() {
        viewModel.todayBangumiList.observe(this::getLifecycle, areaAdapter::submitList)
        viewModel.favoriteBangumiList.observe(this::getLifecycle, favoriteAdapter::submitList)
        viewModel.historyBangumiList.observe(this::getLifecycle, historyAdapter::submitList)
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
                    ID_FAVOURITE -> {
                        findNavController().navigate(
                            HomeFragmentDirections.actionHomeFragmentToBangumiFavoriteFragment()
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
                    ID_MEDIA -> {
                        Timber.tag("Navigator").d("click media")
                        Navigator.navToPlayerMedia(requireActivity())
                    }
                    ID_DOWNLOAD -> {
                        Timber.tag("Navigator").d("click download")
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