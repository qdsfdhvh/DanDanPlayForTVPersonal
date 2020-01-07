package com.dandanplay.tv.ui

import android.graphics.Color
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.dandanplay.tv.R
import com.dandanplay.tv.models.HomeBean
import com.dandanplay.tv.ui.dialog.SelectDialogFragment
import com.dandanplay.tv.ui.dialog.setLoadFragment
import com.dandanplay.tv.ui.download.DownloadManagerActivity
import com.dandanplay.tv.ui.presenter.MainAreaPresenter
import com.dandanplay.tv.ui.presenter.MainMyPresenter
import com.dandanplay.tv.models.AnimeRow
import com.dandanplay.tv.vm.BangumiTimeLineViewModel
import com.seiko.common.ResultData
import com.seiko.common.Status
import com.seiko.common.extensions.lazyAndroid
import com.seiko.domain.model.BangumiIntro
import org.koin.android.viewmodel.ext.android.viewModel

class HomeFragment : BrowseSupportFragment(), OnItemViewClickedListener, View.OnClickListener {

    private val viewModel by viewModel<BangumiTimeLineViewModel>()

    private lateinit var adapterRows: SparseArray<AnimeRow>

    private val leftItems by lazyAndroid {
        listOf(
            HomeBean(ID_AREA, "番剧区", R.drawable.ic_bangumi_area),
//            MyBean(ID_FAVOURITE, "追 番", R.drawable.ic_bangumi_favourite),
            HomeBean(ID_TIME, "放送表", R.drawable.ic_bangumi_time),
//            MyBean(ID_INDEX, "索引", R.drawable.ic_bangumi_index)
            HomeBean(ID_DOWNLOAD, "下载", R.drawable.ic_bangumi_download),
            HomeBean(ID_SETTING, "设置", R.drawable.ic_bangumi_setting)
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
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        title = "弹弹Play"
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
        adapterRows = SparseArray(2)
        adapterRows.put(
            ROW_AREA, AnimeRow(ROW_AREA)
                .setAdapter(MainAreaPresenter())
                .setTitle("今日更新"))
        adapterRows.put(
            ROW_MY, AnimeRow(ROW_MY)
                .setAdapter(MainMyPresenter())
                .setTitle("个人中心"))

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
        // 加载个人数据
        adapterRows.get(ROW_MY)?.setList(leftItems)

        // Navigation在退栈时,回重新调用onCreateView
        if (viewModel.weekBangumiList.value == null) {
            viewModel.getBangumiList()
        }
    }

    /**
     * 加载'今日更新'数据
     */
    private fun updateUI(data: ResultData<List<BangumiIntro>>) {
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
                adapterRows.get(ROW_AREA)?.setList(data.data)
                startEntranceTransition()
            }
        }
    }

    /**
     * 加载退出选择界面
     */
    private fun launchExitDialog() {
        val manager = fragmentManager ?: return
        if (manager.findFragmentByTag(SelectDialogFragment.TAG) == null) {
            SelectDialogFragment.Builder()
                .setTitle("你真的确认退出应用吗？")
                .setConfirmText("确认")
                .setCancelText("取消")
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
            is BangumiIntro -> {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToBangumiDetailsFragment(
                        item.animeId
                    )
                )
            }
            is HomeBean -> {
                when(item.id) {
                    ID_AREA -> {
                        findNavController().navigate(
                            HomeFragmentDirections.actionHomeFragmentToBangumiAreaFragment()
                        )
                    }
                    ID_FAVOURITE -> {
                        ToastUtils.showShort("我的收藏")
                    }
                    ID_TIME -> {
                        findNavController().navigate(
                            HomeFragmentDirections.actionHomeFragmentToBangumiTimeLineFragment()
                        )
                    }
                    ID_INDEX -> {
                        ToastUtils.showShort("索引")
                    }
                    ID_SETTING -> {
                        ToastUtils.showShort("设置")
                    }
                    ID_DOWNLOAD -> {
                        DownloadManagerActivity.launch(activity!!)
                    }
                }
            }
        }
    }

    companion object {
        private const val ROW_AREA = 0
        private const val ROW_MY = 1

        private const val ID_AREA = 0
        private const val ID_FAVOURITE = 1
        private const val ID_TIME = 2
        private const val ID_INDEX = 3
        private const val ID_SETTING = 4
        private const val ID_DOWNLOAD = 5
    }
}