package com.dandanplay.tv2.ui.main

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.ToastUtils
import com.dandanplay.tv2.R
import com.dandanplay.tv2.ui.main.content.BangumiFragment
import com.dandanplay.tv2.ui.main.content.PersonalFragment
import com.dandanplay.tv2.ui.adapter.MainTitleAdapter
import com.dandanplay.tv2.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment: BaseFragment() {

    private var currentPosition = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        val fragments: Array<Fragment> = arrayOf(
            BangumiFragment.newInstance(),
            PersonalFragment.newInstance())
        val fragmentAdapter =
            HomePagerAdapter(childFragmentManager, fragments)

        val titles = arrayOf("番剧", "我的")
        val titleAdapter = MainTitleAdapter(mActivity, titles)
        titleAdapter.setOnItemClickListener(object : MainTitleAdapter.OnItemClickListener {
            override fun onSearchClick() {
                ToastUtils.showShort("搜索")
            }

            override fun onSettingClick() {
                ToastUtils.showShort("设置")
            }

            override fun onTitleFocus(position: Int) {
                val currentItem = view_pager.currentItem
                if (position != currentItem) {
                    view_pager.currentItem = position
                    currentPosition = titleAdapter.getTruePosition(position)
                }
            }
        })

        view_pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                currentPosition = titleAdapter.getTruePosition(position)
//                title_rv.selectedPosition = currentPosition
            }
        })
        view_pager.offscreenPageLimit = 2
        view_pager.adapter = fragmentAdapter

        currentPosition = titleAdapter.getTruePosition(0)
        title_rv.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        title_rv.adapter = titleAdapter
//        title_rv.selectedPosition = currentPosition
        title_rv.requestFocus()
    }

    override fun dispatchKeyEventSupport(event: KeyEvent?): Boolean {
        if (event != null) {
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (event.keyCode == KeyEvent.KEYCODE_BACK) {
                    val focusedChild = title_rv.focusedChild
                    if (focusedChild == null) {
//                        title_rv.selectedPosition = currentPosition
                        title_rv.requestFocus()
                        return true
                    }
//                    else if (currentPosition != 0) {
//                        title_rv.selectedPosition = 0
//                        title_rv.requestFocus()
//                        return true
//                    }
                }
            }
        }
        return false
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}

class HomePagerAdapter(manager: FragmentManager, private val fragments: Array<Fragment>
) : FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

}