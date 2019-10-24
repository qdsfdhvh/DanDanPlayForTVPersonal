package com.dandanplay.tv2.ui.main.content

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.dandanplay.tv2.R

import com.dandanplay.tv2.bean.MyBean
import com.dandanplay.tv2.helper.MainPaddingDecoration
import com.dandanplay.tv2.ui.adapter.MainMyAdapter
import com.dandanplay.tv2.ui.base.BaseFragment
import kotlinx.android.synthetic.main.layout_common_recycler_view.*

class PersonalFragment : BaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val items = arrayOf(
            MyBean(
                ID_AVATAR,
                "登录",
                R.drawable.ic_user_center_default_avatar,
                R.drawable.background_main_my_avatar
            ),
            MyBean(
                ID_HISTORY,
                "我的历史",
                R.drawable.ic_user_center_history,
                R.drawable.background_main_my_avatar
            ),
            MyBean(
                ID_START,
                "我的收藏",
                R.drawable.ic_user_center_star,
                R.drawable.background_main_my_avatar
            ),
            MyBean(
                ID_FOLLOW,
                "我的关注",
                R.drawable.ic_user_center_follow_bangumi,
                R.drawable.background_main_my_avatar
            )
        )
        val adapter = MainMyAdapter(mActivity, items)
        adapter.setOnItemClickListener(object : MainMyAdapter.OnItemClickListener {
            override fun onItemClick(itemView: View, id: Int) {
                when(id) {
                    ID_AVATAR -> {
                        ToastUtils.showShort("登录")
                    }
                    ID_HISTORY -> {
                        ToastUtils.showShort("我的历史")
                    }
                    ID_START -> {
                        ToastUtils.showShort("我的收藏")
                    }
                    ID_FOLLOW -> {
                        ToastUtils.showShort("我的关注")
                    }
                }
            }
        })
        recycler_view.layoutManager = GridLayoutManager(mActivity, 4)
        recycler_view.addItemDecoration(MainPaddingDecoration(mActivity))
        recycler_view.adapter = adapter
        recycler_view.requestFocus()
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_common_recycler_view
    }

    companion object {
        private const val ID_AVATAR = 0
        private const val ID_HISTORY = 1
        private const val ID_START = 2
        private const val ID_FOLLOW = 3

        fun newInstance() = PersonalFragment()
    }

}