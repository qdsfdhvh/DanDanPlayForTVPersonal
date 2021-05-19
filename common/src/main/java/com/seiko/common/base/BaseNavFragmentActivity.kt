package com.seiko.common.base

import android.os.Bundle
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import androidx.fragment.app.proxyFragmentFactory
import androidx.navigation.fragment.NavHostFragment
import com.seiko.common.R
import com.seiko.common.util.extensions.hasFragment

abstract class BaseNavFragmentActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        proxyFragmentFactory()
        super.onCreate(savedInstanceState)

        val layoutId: Int = R.id.container
        setContentView(FragmentContainerView(this).apply {
            id = layoutId
        })

        if (!hasFragment(layoutId)) {
            supportFragmentManager.commit {
                val navHostFragment = NavHostFragment.create(0)
                add(layoutId, navHostFragment)
                setPrimaryNavigationFragment(navHostFragment)

                runOnCommit {
                    onLoadRoot(navHostFragment)
                }
            }
        }
    }

    abstract fun onLoadRoot(navHostFragment: NavHostFragment)
}