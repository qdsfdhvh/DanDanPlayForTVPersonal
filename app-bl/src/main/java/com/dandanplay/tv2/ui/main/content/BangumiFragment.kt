package com.dandanplay.tv2.ui.main.content

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.layout.*
import com.blankj.utilcode.util.ToastUtils
import com.dandanplay.tv2.R
import com.dandanplay.tv2.bean.MyBean
import com.dandanplay.tv2.ui.adapter.MainBangumiLeftAdapter
import com.dandanplay.tv2.ui.adapter.MainBangumiRightAdapter
import com.dandanplay.tv2.ui.base.BaseFragment
import com.dandanplay.tv2.ui.dialog.setLoadFragment
import com.seiko.common.ResultData
import com.seiko.common.Status
import com.seiko.common.lazyAndroid
import com.dandanplay.tv2.vm.BangumiAViewModel
import com.seiko.domain.entities.BangumiIntro
import com.seiko.widget.tv.VLayoutManager
import kotlinx.android.synthetic.main.layout_common_recycler_view.*
import org.koin.android.viewmodel.ext.android.viewModel

class BangumiFragment : BaseFragment() {

    private val viewModel by viewModel<BangumiAViewModel>()

    private var lastForceView: View? = null

//    private val adapters by lazyAndroid { LinkedList<DelegateAdapter.Adapter<*>>() }

    private lateinit var adapter: DelegateAdapter

    private val leftItems by lazyAndroid {
        listOf(
            MyBean(
                0, "番剧区",
                R.drawable.ic_bangumi_area,
                R.drawable.background_main_my_avatar
            ),
            MyBean(
                0, "追 番",
                R.drawable.ic_bangumi_favourite,
                R.drawable.background_main_my_avatar
            ),
            MyBean(
                0, "放送表",
                R.drawable.ic_bangumi_time,
                R.drawable.background_main_my_avatar
            ),
            MyBean(
                0, "索引",
                R.drawable.ic_bangumi_index,
                R.drawable.background_main_my_avatar
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.mainState.observe(this::getLifecycle, this::updateUI)

        recycler_view.setBackgroundResource(R.color.black_30)

        val layoutManger = VLayoutManager(mActivity, VLayoutManager.HORIZONTAL)
        recycler_view.layoutManager = layoutManger

        adapter = DelegateAdapter(layoutManger)
        recycler_view.adapter = adapter

//        recycler_view.setPadding(
//            mActivity.resources.getDimensionPixelSize(R.dimen.px_20),
//            mActivity.resources.getDimensionPixelSize(R.dimen.px_64),
//            mActivity.resources.getDimensionPixelSize(R.dimen.px_20),
//            0)

        //  0  4  5
        //  1
        //  2
        //  3
//        val dividerTop = mActivity.resources.getDimensionPixelSize(R.dimen.px_64)
//        val dividerHeight = mActivity.resources.getDimensionPixelSize(R.dimen.px_20)
//        recycler_view.addItemDecoration(object : RecyclerView.ItemDecoration() {
//            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
//                super.getItemOffsets(outRect, view, parent, state)
//                when(val position = parent.getChildAdapterPosition(view)) {
//                    0 -> {
//                        outRect.left = dividerHeight
//                        outRect.top = dividerTop
//                        outRect.right = dividerHeight / 2
//                        outRect.bottom = dividerHeight / 2
//                    }
//                    1, 2 -> {
//                        outRect.left = dividerHeight
//                        outRect.top = dividerHeight / 2
//                        outRect.right = dividerHeight / 2
//                        outRect.bottom = dividerHeight / 2
//                    }
//                    3 -> {
//                        outRect.left = dividerHeight
//                        outRect.top = dividerHeight / 2
//                        outRect.right = dividerHeight / 2
//                        outRect.bottom = 0
//                    }
//                    else -> {
//                        if (position == adapter.itemCount - 1) {
//                            outRect.left = dividerHeight / 2
//                            outRect.top = dividerTop
//                            outRect.right = dividerHeight
//                            outRect.bottom = 0
//                        } else {
//                            outRect.left = dividerHeight / 2
//                            outRect.top = dividerTop
//                            outRect.right = dividerHeight / 2
//                            outRect.bottom = 0
//                        }
//                    }
//                }
//            }
//        })
        val helper = GridLayoutHelper(4, 4,
            mActivity.resources.getDimensionPixelSize(R.dimen.px_20),
            mActivity.resources.getDimensionPixelSize(R.dimen.px_20))
        adapter.addAdapter(MainBangumiLeftAdapter(mActivity, leftItems, helper))
    }

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        viewModel.getBangumiList()
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
                val items = data.data ?: return

                val helper = LinearLayoutHelper(mActivity.resources.getDimensionPixelSize(R.dimen.px_20))
                helper.bgColor = Color.BLUE
                adapter.addAdapter(MainBangumiRightAdapter(mActivity, items, helper))
            }
        }
    }

    override fun onSupportVisible() {
        if (lastForceView != null) {
            lastForceView!!.requestFocus()
            lastForceView = null
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_common_recycler_view
    }

    companion object {
        fun newInstance() = BangumiFragment()
    }

}