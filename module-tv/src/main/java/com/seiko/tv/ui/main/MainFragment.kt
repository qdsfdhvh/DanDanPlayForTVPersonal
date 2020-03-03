package com.seiko.tv.ui.main

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import com.seiko.common.router.Navigator
import com.seiko.common.ui.adapter.AsyncObjectAdapter
import com.seiko.common.ui.adapter.AsyncPagedObjectAdapter
import com.seiko.common.ui.dialog.DialogSelectFragment
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.common.util.toast.toast
import com.seiko.tv.R
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.data.model.HomeSettingBean
import com.seiko.tv.ui.area.BangumiAreaActivity
import com.seiko.tv.ui.bangumi.*
import com.seiko.tv.ui.card.MainAreaCardView
import com.seiko.tv.ui.presenter.BangumiPresenterSelector
import com.seiko.tv.ui.search.SearchActivity
import com.seiko.tv.util.diff.HomeImageBeanDiffCallback
import com.seiko.tv.vm.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import org.koin.android.viewmodel.ext.android.viewModel

class MainFragment : BrowseSupportFragment()
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
        private const val ID_SETTING = 3
        private const val ID_DOWNLOAD = 4
        private const val ID_HISTORY = 5
        private const val ID_MEDIA = 6

        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    private val viewModel: HomeViewModel by viewModel()

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

    override fun onDestroyView() {
        super.onDestroyView()
        unBindViewModel()
    }

    /**
     * 生成相关UI
     */
    private fun setupUI() {
//        headersState = HEADERS_ENABLED
//        isHeadersTransitionOnBackEnabled = true
        title = getString(R.string.app_name)
        brandColor = Color.parseColor("#424242")

        // 设置搜索键
        searchAffordanceColor = ContextCompat.getColor(requireActivity(), R.color.colorAccent)
        setOnSearchClickedListener(this)

        // Item点击
        onItemViewClickedListener = this

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

        adapter = rowsAdapter

        prepareEntranceTransition()
    }

    private fun  createListRow(id: Int, title: String, objectAdapter: ObjectAdapter) {
        val headerItem = HeaderItem(id.toLong(), title)
        val listRow = ListRow(headerItem, objectAdapter)
        R.layout.lb_rows_fragment
        rowsAdapter.add(listRow)
    }

    /**
     * 开始加载数据
     */
    private fun bindViewModel() {
        viewModel.todayBangumiList.observe(this::getLifecycle) { list ->
            lifecycleScope.launchWhenStarted {
                yield()
                startEntranceTransition()
                areaAdapter.submitList(list)
            }
        }
        viewModel.favoriteBangumiList.observe(this::getLifecycle) { list ->
            lifecycleScope.launchWhenStarted {
                yield()
                favoriteAdapter.submitList(list)
            }
        }
        viewModel.historyBangumiList.observe(this::getLifecycle) { list ->
            lifecycleScope.launchWhenStarted {
                yield()
                historyAdapter.submitList(list)
            }
        }
    }

    private fun unBindViewModel() {
        viewModel.todayBangumiList.removeObservers(this::getLifecycle)
        viewModel.favoriteBangumiList.removeObservers(this::getLifecycle)
        viewModel.historyBangumiList.removeObservers(this::getLifecycle)
    }

    /**
     * PS: 控件id查看 {@link [androidx.leanback.widget.TitleView]}
     */
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.title_orb -> {
                SearchActivity.launchBangumi(requireActivity())
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
                val cardView = holder.view as MainAreaCardView
                BangumiDetailsActivity.launch(requireActivity(), item.animeId, cardView.getImageView())
            }
            is HomeSettingBean -> {
                when(item.id) {
                    ID_AREA -> {
                        BangumiAreaActivity.launch(requireActivity())
                    }
                    ID_FAVOURITE -> {
                        BangumiFavoriteActivity.launch(requireActivity())
                    }
                    ID_TIME -> {
                        BangumiTimeLineActivity.launch(requireActivity())
                    }
                    ID_HISTORY -> {
                        BangumiHistoryActivity.launch(requireActivity())
                    }
                    ID_MEDIA -> {
                        Navigator.navToPlayerMedia(requireActivity())
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