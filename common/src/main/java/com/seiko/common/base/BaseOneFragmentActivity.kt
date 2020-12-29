package com.seiko.common.base

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import com.seiko.common.R
import com.seiko.common.util.extensions.hasFragment

/**
 * 单个Fragment的Activity简单封装
 */
abstract class BaseOneFragmentActivity : BaseActivity() {

    @IdRes
    protected open val layoutId: Int = R.id.container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(onCreateFragmentContainerView())
        if (!hasFragment(layoutId)) {
            supportFragmentManager.commit {
                add(layoutId, onCreateFragment())
            }
        }
    }

    protected open fun onCreateFragmentContainerView(): FragmentContainerView {
        return FragmentContainerView(this).apply {
            id = layoutId
        }
    }

    abstract fun onCreateFragment(): Fragment

}