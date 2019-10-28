package com.dandanplay.tv.ui

import android.graphics.Color
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.dandanplay.tv.R
import com.dandanplay.tv.bean.MyBean
import com.dandanplay.tv.ui.dialog.ExitDialogFragment
import com.dandanplay.tv.ui.dialog.setLoadFragment
import com.dandanplay.tv.ui.presenter.MainAreaPresenter
import com.dandanplay.tv.ui.presenter.MainMyPresenter
import com.dandanplay.tv.utils.AnimeRow
import com.dandanplay.tv.vm.BangumiViewModel
import com.seiko.common.ResultData
import com.seiko.common.Status
import com.seiko.common.lazyAndroid
import com.seiko.domain.entity.BangumiIntro
import org.koin.android.viewmodel.ext.android.viewModel

class HomeFragment : BrowseSupportFragment(), OnItemViewClickedListener, View.OnClickListener {

    private val viewModel by viewModel<BangumiViewModel>()

    private lateinit var adapterRows: SparseArray<AnimeRow>

    private val leftItems by lazyAndroid {
        listOf(
            MyBean(ID_AREA, "番剧区", R.drawable.ic_bangumi_area),
            MyBean(ID_FAVOURITE, "追 番", R.drawable.ic_bangumi_favourite),
            MyBean(ID_TIME, "放送表", R.drawable.ic_bangumi_time),
            MyBean(ID_INDEX, "索引", R.drawable.ic_bangumi_index)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LogUtils.d("onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        LogUtils.d("onDestroyView")
        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.mainState.observe(this::getLifecycle, this::updateUI)
        // 基本参数
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        title = "弹弹Play"
        brandColor = Color.parseColor("#424242")

        // 设置搜索键
        searchAffordanceColor = ContextCompat.getColor(activity!!, R.color.colorAccent)
        setOnSearchClickedListener(this)

        // 生成数据的Adapter
        adapterRows = SparseArray(2)
        adapterRows.put(ROW_AREA, AnimeRow(ROW_AREA)
            .setAdapter(MainAreaPresenter())
            .setTitle("今日更新"))
        adapterRows.put(ROW_MY, AnimeRow(ROW_MY)
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

        // 监听返回键
        requireActivity().onBackPressedDispatcher.addCallback(this) { launchExitDialog() }

        // 加载个人数据
        adapterRows.get(ROW_MY)?.setList(leftItems)

        // Navigation在退栈时,回重新调用onCreateView, onViewCreated
        if (viewModel.mainState.value == null) {
            viewModel.getBangumiList()
        }
    }

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
                adapterRows.get(ROW_AREA)?.setList(data.data ?: return)
                startEntranceTransition()
            }
        }
    }

    private fun launchExitDialog() {
        val manager = fragmentManager ?: return
        if (manager.findFragmentByTag(ExitDialogFragment.TAG) == null) {
            ExitDialogFragment.Builder()
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

    override fun onItemClicked(holder: Presenter.ViewHolder,
                               item: Any?,
                               rowHolder: RowPresenter.ViewHolder?,
                               row: Row?) {
        when(item) {
            is BangumiIntro -> {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToBangumiDetailsFragment(item.animeId)
                )
            }
            is MyBean -> {
                when(item.id) {
                    ID_AREA -> {
                        ToastUtils.showShort("番剧")
                    }
                    ID_FAVOURITE -> {
                        ToastUtils.showShort("我的收藏")
                    }
                    ID_TIME -> {
                        ToastUtils.showShort("放送表")
                    }
                    ID_INDEX -> {
                        ToastUtils.showShort("索引")
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
    }
}