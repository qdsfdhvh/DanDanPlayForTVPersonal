package com.dandanplay.tv2.ui.main

import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.dandanplay.tv2.R
import com.dandanplay.tv2.ui.base.BaseActivity
import com.dandanplay.tv2.ui.base.findFragment
import com.dandanplay.tv2.ui.base.loadRootFragment
import com.dandanplay.tv2.ui.dialog.ExitDialogFragment
import com.dandanplay.tv2.vm.MainViewModel
import me.yokeyword.fragmentation.anim.FragmentAnimator
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (findFragment(HomeFragment::class.java) == null) {
            loadRootFragment(R.id.container,
                HomeFragment.newInstance()
            )
        }
        viewModel.test()
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return FragmentAnimator(
            R.anim.v_fragment_enter,
            R.anim.v_fragment_exit,
            R.anim.v_fragment_smart_pop_enter,
            R.anim.v_fragment_pop_exit)
    }

    override fun onBackPressedSupport() {
        if (supportFragmentManager.findFragmentByTag(ExitDialogFragment.TAG) == null) {
            ExitDialogFragment.Builder()
                .setTitle("你真的确认退出应用吗？")
                .setConfirmText("确认")
                .setCancelText("取消")
                .setConfirmClickListener(View.OnClickListener {
                    ActivityUtils.finishActivity(this, true)
                })
                .build()
                .show(supportFragmentManager)
        }
    }

}
