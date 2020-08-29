package com.seiko.common.base

import android.os.Build
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentActivity
import com.seiko.common.util.fix.FixActivityFromTransitionManager
import com.seiko.common.util.fix.LeakFreeSupportSharedElementCallback

abstract class BaseActivity : FragmentActivity {

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FixActivityFromTransitionManager.removeActivityFromTransitionManager(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setEnterSharedElementCallback(LeakFreeSupportSharedElementCallback())
            setExitSharedElementCallback(LeakFreeSupportSharedElementCallback())
        }
    }
}