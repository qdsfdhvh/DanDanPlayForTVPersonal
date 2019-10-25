package com.dandanplay.tv.ui

import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.dandanplay.tv.R
import com.dandanplay.tv.ui.base.*
import com.dandanplay.tv.ui.dialog.ExitDialogFragment


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (findFragment(MainFragment::class.java) == null) {
            loadRootFragment(R.id.container, MainFragment.newInstance())
        }
    }

    override fun onBackPressedSupport() {
//        if (supportFragmentManager.backStackEntryCount > 1) {
//            pop()
//            return
//        }

        if (supportFragmentManager.findFragmentByTag(ExitDialogFragment.TAG) == null) {
            ExitDialogFragment.Builder()
                .setTitle("你真的确认退出应用吗？")
                .setConfirmText("确认")
                .setCancelText("取消")
                .setConfirmClickListener {
                    ActivityUtils.finishActivity(this)
                }
                .build()
                .show(supportFragmentManager)
        }
    }

}