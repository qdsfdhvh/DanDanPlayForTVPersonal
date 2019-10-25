package com.dandanplay.tv.ui.base

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.BrowseSupportFragment
import com.seiko.common.lazyAndroid
import me.yokeyword.fragmentation.*
import me.yokeyword.fragmentation.anim.FragmentAnimator

abstract class SupportBrowerFragment: BrowseSupportFragment(), ISupportFragment {

    protected lateinit var mActivity: FragmentActivity

    private val delegate by lazyAndroid { SupportFragmentDelegate(this) }

    override fun getSupportDelegate(): SupportFragmentDelegate {
        return delegate
    }

    override fun extraTransaction(): ExtraTransaction {
        return extraTransaction()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentActivity) {
            delegate.onAttach(context)
            mActivity = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        delegate.onCreate(savedInstanceState)
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return delegate.onCreateAnimation(transit, enter, nextAnim)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        delegate.onActivityCreated(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        delegate.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        delegate.onResume()
    }

    override fun onPause() {
        super.onPause()
        delegate.onPause()
    }

    override fun onDestroyView() {
        delegate.onDestroyView()
        super.onDestroyView()
    }

    override fun onDestroy() {
        delegate.onDestroy()
        super.onDestroy()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        delegate.onHiddenChanged(hidden)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        delegate.setUserVisibleHint(isVisibleToUser)
    }

    override fun enqueueAction(runnable: Runnable?) {
        delegate.enqueueAction(runnable)
    }

    override fun post(runnable: Runnable?) {
        delegate.post(runnable)
    }

    override fun onEnterAnimationEnd(savedInstanceState: Bundle?) {
        delegate.onEnterAnimationEnd(savedInstanceState)
    }

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        delegate.onLazyInitView(savedInstanceState)
    }

    override fun onSupportVisible() {
        delegate.onSupportVisible()
    }

    override fun onSupportInvisible() {
        delegate.onSupportInvisible()
    }

    override fun isSupportVisible(): Boolean {
        return delegate.isSupportVisible
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return delegate.onCreateFragmentAnimator()
    }

    override fun getFragmentAnimator(): FragmentAnimator {
        return delegate.fragmentAnimator
    }

    override fun setFragmentAnimator(fragmentAnimator: FragmentAnimator?) {
        delegate.fragmentAnimator = fragmentAnimator
    }

    override fun onBackPressedSupport(): Boolean {
        return delegate.onBackPressedSupport()
    }

    override fun setFragmentResult(resultCode: Int, bundle: Bundle?) {
        delegate.setFragmentResult(resultCode, bundle)
    }

    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) {
        delegate.onFragmentResult(requestCode, resultCode, data)
    }

    override fun onNewBundle(args: Bundle?) {
        delegate.onNewBundle(args)
    }

    override fun putNewBundle(newBundle: Bundle?) {
        delegate.putNewBundle(newBundle)
    }

    open fun dispatchKeyEventSupport(event: KeyEvent?): Boolean {
        return false
    }
}