package com.dandanplay.tv.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.dandanplay.tv.R
import com.dandanplay.tv.model.HomeSettingBean
import com.dandanplay.tv.ui.dialog.DialogExitFragment
import com.seiko.common.ui.dialog.setLoadFragment
import com.dandanplay.tv.ui.presenter.MainAreaPresenter
import com.dandanplay.tv.ui.presenter.MainSettingPresenter
import com.dandanplay.tv.model.AnimeRow
import com.dandanplay.tv.model.HomeImageBean
import com.dandanplay.tv.util.diff.HomeImageBeanDiffCallback
import com.dandanplay.tv.vm.HomeViewModel
import com.seiko.common.ResultData
import com.seiko.common.Status
import com.seiko.common.extensions.lazyAndroid
import com.seiko.common.router.Navigator
import org.koin.android.viewmodel.ext.android.viewModel

class HomeFragment : BrowseSupportFragment(), OnItemViewClickedListener, View.OnClickListener {

    private val viewModel by viewModel<HomeViewModel>()

    private lateinit var adapterRows: SparseArray<AnimeRow<*>>

    private lateinit var navController: NavController

    private val leftItems by lazyAndroid {
        listOf(
            HomeSettingBean(ID_AREA, getString(R.string.bangumi_area), R.drawable.ic_bangumi_area),
//            MyBean(ID_FAVOURITE, "追 番", R.drawable.ic_bangumi_favourite),
            HomeSettingBean(ID_TIME, getString(R.string.bangumi_time), R.drawable.ic_bangumi_time),
//            MyBean(ID_INDEX, "索引", R.drawable.ic_bangumi_index)
            HomeSettingBean(ID_DOWNLOAD, getString(R.string.bangumi_download), R.drawable.ic_bangumi_download),
            HomeSettingBean(ID_SETTING, getString(R.string.bangumi_setting), R.drawable.ic_bangumi_setting)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupRows()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindViewModel()
    }

    /**
     * 生成相关UI
     */
    private fun setupUI() {
        navController = findNavController()
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        title = getString(R.string.app_name)
        brandColor = Color.parseColor("#424242")

        // 设置搜索键
        searchAffordanceColor = ContextCompat.getColor(activity!!, R.color.colorAccent)
        setOnSearchClickedListener(this)

        // 监听返回键
        requireActivity().onBackPressedDispatcher.addCallback(this) { launchExitDialog() }
    }

    /**
     * 生成Rows
     */
    private fun setupRows() {
        if (adapter != null) return
        // 生成数据的Adapter
        adapterRows = SparseArray(3)
        adapterRows.put(ROW_AREA, AnimeRow<HomeImageBean>(ROW_AREA)
            .setDiffCallback(HomeImageBeanDiffCallback())
            .setAdapter(MainAreaPresenter())
            .setTitle(getString(R.string.title_area)))
        adapterRows.put(ROW_FAVORITE, AnimeRow<HomeImageBean>(ROW_FAVORITE)
            .setDiffCallback(HomeImageBeanDiffCallback())
            .setAdapter(MainAreaPresenter())
            .setTitle(getString(R.string.title_favorite)))
        adapterRows.put(ROW_SETTING, AnimeRow<HomeSettingBean>(ROW_SETTING)
                .setAdapter(MainSettingPresenter())
                .setTitle(getString(R.string.title_setting)))

        // 生成界面的Adapter
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        for (i in 0 until adapterRows.size()) {
            val row = adapterRows.valueAt(i)
            val headerItem = HeaderItem(row.getId(), row.getTitle())
            val listRow = ListRow(headerItem, row.getAdapter())
            rowsAdapter.add(listRow)
        }

        // 绑定Adapter
        adapter = rowsAdapter
        onItemViewClickedListener = this
    }

    /**
     * 开始加载数据
     */
    private fun bindViewModel() {
        viewModel.weekBangumiList.observe(this::getLifecycle, this::updateUI)
        viewModel.favoriteBangumiList.observe(this::getLifecycle) { favorites ->
            adapterRows.get(ROW_FAVORITE)?.setList(favorites)
        }
        // 加载个人数据
        adapterRows.get(ROW_SETTING)?.setList(leftItems)

        // Navigation在退栈时,回重新调用onCreateView
        viewModel.getBangumiList(false)
        viewModel.getFavoriteBangumiList()
    }

    /**
     * 加载'今日更新'数据
     */
    private fun updateUI(data: ResultData<List<HomeImageBean>>) {
        when(data.responseType) {
            Status.LOADING -> {
                setLoadFragment(true)
            }
            Status.ERROR -> {
                setLoadFragment(false)
                ToastUtils.showShort(data.error?.message)
            }
            Status.SUCCESSFUL -> {
                setLoadFragment(false)
                adapterRows.get(ROW_AREA)?.setList(data.data)
//                startEntranceTransition()
            }
        }
    }

    /**
     * 加载退出选择界面
     */
    private fun launchExitDialog() {
        val manager = fragmentManager ?: return
        if (manager.findFragmentByTag(DialogExitFragment.TAG) == null) {
            DialogExitFragment.Builder()
                .setTitle(getString(R.string.msg_exit_app))
                .setConfirmText(getString(R.string.exit))
                .setCancelText(getString(R.string.cancel))
                .setConfirmClickListener {
                    ActivityUtils.finishActivity(requireActivity(), true)
                }
                .build()
                .show(manager)
        }
    }

    /**
     * PS: 控件id查看 {@link [androidx.leanback.widget.TitleView]}
     */
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.title_orb -> {
                navController.navigate(
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
                navController.navigate(
                    HomeFragmentDirections.actionHomeFragmentToBangumiDetailsFragment(
                        item.animeId
                    )
                )
            }
            is HomeSettingBean -> {
                when(item.id) {
                    ID_AREA -> {
                        navController.navigate(
                            HomeFragmentDirections.actionHomeFragmentToBangumiAreaFragment()
                        )
                    }
                    ID_TIME -> {
                        navController.navigate(
                            HomeFragmentDirections.actionHomeFragmentToBangumiTimeLineFragment()
                        )
                    }
                    ID_DOWNLOAD -> {
                        Navigator.navToTorrent()
                    }
                }
            }
        }
    }

    companion object {
        private const val ROW_AREA = 0
        private const val ROW_FAVORITE = 1
        private const val ROW_SETTING = 2

        private const val ID_AREA = 0
        private const val ID_FAVOURITE = 1
        private const val ID_TIME = 2
        private const val ID_INDEX = 3
        private const val ID_SETTING = 4
        private const val ID_DOWNLOAD = 5
    }
}