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
import com.seiko.common.ui.dialog.setLoadFragment
import com.seiko.tv.ui.presenter.MainAreaPresenter
import com.seiko.tv.ui.presenter.MainSettingPresenter
import com.seiko.tv.data.model.AnimeRow
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.util.diff.HomeImageBeanDiffCallback
import com.seiko.tv.vm.HomeViewModel
import com.seiko.common.data.ResultData
import com.seiko.common.data.Status
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.common.router.Navigator
import com.seiko.common.util.toast.toast
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class HomeFragment : FixBrowseSupportFragment()
    , OnItemViewClickedListener
    , View.OnClickListener {

    private val viewModel by viewModel<HomeViewModel>()

    private var adapterRows: SparseArray<AnimeRow<*>>? = null

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupUI()
        setupRows()
        bindViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapterRows = null
    }

    /**
     * 生成相关UI
     */
    private fun setupUI() {
//        navController = findNavController()
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
//        if (adapter != null) return
        // 生成数据的Adapter
        adapterRows = SparseArray(3)
        adapterRows!!.put(ROW_AREA, AnimeRow<HomeImageBean>(ROW_AREA)
            .setDiffCallback(HomeImageBeanDiffCallback())
            .setAdapter(MainAreaPresenter())
            .setTitle(getString(R.string.title_area)))
        adapterRows!!.put(ROW_FAVORITE, AnimeRow<HomeImageBean>(ROW_FAVORITE)
            .setDiffCallback(HomeImageBeanDiffCallback())
            .setAdapter(MainAreaPresenter())
            .setTitle(getString(R.string.title_favorite)))
        adapterRows!!.put(ROW_SETTING, AnimeRow<HomeSettingBean>(ROW_SETTING)
                .setAdapter(MainSettingPresenter())
                .setTitle(getString(R.string.title_setting)))

        // 生成界面的Adapter
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        for (i in 0 until adapterRows!!.size()) {
            val row = adapterRows!!.valueAt(i)
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
            adapterRows?.get(ROW_FAVORITE)?.setList(favorites)
        }
        // 加载个人数据
        adapterRows?.get(ROW_SETTING)?.setList(leftItems)

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
                toast(data.error?.message)
                Timber.e(data.error)
            }
            Status.SUCCESSFUL -> {
                setLoadFragment(false)
                adapterRows?.get(ROW_AREA)?.setList(data.data)
//                startEntranceTransition()
            }
        }
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
                        findNavController().navigate(
                            HomeFragmentDirections.actionHomeFragmentToBangumiAreaFragment()
                        )
                    }
                    ID_TIME -> {
                        findNavController().navigate(
                            HomeFragmentDirections.actionHomeFragmentToBangumiTimeLineFragment()
                        )
                    }
                    ID_DOWNLOAD -> {
                        Navigator.navToTorrent(requireActivity())
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