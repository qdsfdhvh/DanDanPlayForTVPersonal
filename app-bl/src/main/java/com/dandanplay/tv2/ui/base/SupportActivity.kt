package com.dandanplay.tv2.ui.base

import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import com.seiko.common.lazyAndroid
import me.yokeyword.fragmentation.*
import me.yokeyword.fragmentation.anim.FragmentAnimator

abstract class SupportActivity: AppCompatActivity(), ISupportActivity {

    private val delegate by lazyAndroid { SupportActivityDelegate(this) }

    override fun getSupportDelegate(): SupportActivityDelegate {
        return delegate
    }

    override fun extraTransaction(): ExtraTransaction {
        return delegate.extraTransaction()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        delegate.onCreate(savedInstanceState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        delegate.onPostCreate(savedInstanceState)
    }

    override fun onDestroy() {
        delegate.onDestroy()
        super.onDestroy()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return delegate.dispatchTouchEvent(ev) || super.dispatchTouchEvent(ev)
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        val activeFragment = SupportHelper.getActiveFragment(supportFragmentManager) as? SupportFragment
        return dispatchKeyEventSupport(activeFragment, event) || super.dispatchKeyEvent(event)
    }

    final override fun onBackPressed() {
        delegate.onBackPressed()
    }

    override fun onBackPressedSupport() {
        delegate.onBackPressedSupport()
    }

    override fun getFragmentAnimator(): FragmentAnimator {
        return delegate.fragmentAnimator
    }

    override fun setFragmentAnimator(fragmentAnimator: FragmentAnimator?) {
        delegate.fragmentAnimator = fragmentAnimator
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return delegate.onCreateFragmentAnimator()
    }

    override fun post(runnable: Runnable?) {
        delegate.post(runnable)
    }

}

private fun dispatchKeyEventSupport(activeFragment: SupportFragment?, event: KeyEvent?): Boolean {
    if (activeFragment != null) {
        val result = activeFragment.dispatchKeyEventSupport(event)
        if (result) {
            return true
        }

        val parentFragment = activeFragment.parentFragment
        if (dispatchKeyEventSupport(
                parentFragment as? SupportFragment,
                event
            )
        ) {
            return true
        }
    }
    return false
}

/**
 * 加载根Fragment, 即Activity内的第一个Fragment 或 Fragment内的第一个子Fragment
 *
 * @param containerId 容器id
 * @param toFragment  目标Fragment
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportActivity.loadRootFragment(containerId: Int,
                                                                       toFragment: ISupportFragment) {
    supportDelegate.loadRootFragment(containerId, toFragment)
}

@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportActivity.loadRootFragment(containerId: Int,
                                                                       toFragment: ISupportFragment,
                                                                       addToBackStack: Boolean) {
    supportDelegate.loadRootFragment(containerId, toFragment, addToBackStack, false)
}

@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportActivity.loadRootFragment(containerId: Int,
                                                                       toFragment: ISupportFragment,
                                                                       addToBackStack: Boolean,
                                                                       allowAnimation: Boolean) {
    supportDelegate.loadRootFragment(containerId, toFragment, addToBackStack, allowAnimation)
}

/**
 * 加载多个同级根Fragment,类似Wechat, QQ主页的场景
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportActivity.loadMultipleRootFragment(containerId: Int,
                                                                               showPosition: Int,
                                                                               fragments: Array<ISupportFragment>) {
    supportDelegate.loadMultipleRootFragment(containerId, showPosition, *fragments)
}

/**
 * show一个Fragment,hide其他同栈所有Fragment
 * 使用该方法时，要确保同级栈内无多余的Fragment,(只有通过loadMultipleRootFragment()载入的Fragment)
 * <p>
 * 建议使用更明确的{@link #showHideFragment(ISupportFragment, ISupportFragment)}
 *
 * @param show 需要show的Fragment
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportActivity.showHideFragment(show: ISupportFragment) {
    supportDelegate.showHideFragment(show)
}

@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportActivity.showHideFragment(show: ISupportFragment, hide: ISupportFragment?) {
    supportDelegate.showHideFragment(show, hide)
}

/**
 * It is recommended to use {@link SupportFragment#start(ISupportFragment)}.
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportActivity.start(toFragment: ISupportFragment) {
    supportDelegate.start(toFragment)
}

@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportActivity.start(toFragment: ISupportFragment,
                                                            @ISupportFragment.LaunchMode launchModel: Int) {
    supportDelegate.start(toFragment, launchModel)
}

/**
 * It is recommended to use {@link SupportFragment#startForResult(ISupportFragment, int)}.
 * Launch an fragment for which you would like a result when it poped.
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportActivity.startForResult(toFragment: ISupportFragment,
                                                                     requestCode: Int) {
    supportDelegate.startForResult(toFragment, requestCode)
}

/**
 * It is recommended to use {@link SupportFragment#startWithPop(ISupportFragment)}.
 * Start the target Fragment and pop itself
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportActivity.startWithPop(toFragment: ISupportFragment) {
    supportDelegate.startWithPop(toFragment)
}

/**
 * It is recommended to use {@link SupportFragment#startWithPopTo(ISupportFragment, Class, boolean)}.
 *
 * @see #popTo(Class, boolean)
 * +
 * @see #start(ISupportFragment)
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportActivity.startWithPopTo(toFragment: ISupportFragment,
                                                                     targetFragmentClass: Class<*>,
                                                                     includeTargetFragment: Boolean) {
    supportDelegate.startWithPopTo(toFragment, targetFragmentClass, includeTargetFragment)
}

/**
 * It is recommended to use {@link SupportFragment#replaceFragment(ISupportFragment, boolean)}.
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportActivity.replaceFragment(toFragment: ISupportFragment, addToBackStack: Boolean) {
    supportDelegate.replaceFragment(toFragment, addToBackStack)
}

/**
 * Pop the fragment.
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportActivity.pop() {
    supportDelegate.pop()
}

/**
 * Pop the last fragment transition from the manager's fragment
 * back stack.
 * <p>
 * 出栈到目标fragment
 *
 * @param targetFragmentClass   目标fragment
 * @param includeTargetFragment 是否包含该fragment
 */
@JvmOverloads
fun SupportActivity.popTo(targetFragmentClass: Class<*>,
                                                     includeTargetFragment: Boolean,
                                                     afterPopTransactionRunnable: Runnable? = null,
                                                     popAnim: Int = Integer.MAX_VALUE) {
    supportDelegate.popTo(targetFragmentClass, includeTargetFragment,
        afterPopTransactionRunnable, popAnim)
}

/**
 * 当Fragment根布局 没有 设定background属性时,
 * Fragmentation默认使用Theme的android:windowbackground作为Fragment的背景,
 * 可以通过该方法改变其内所有Fragment的默认背景。
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportActivity.setDefaultFragmentBackground(@DrawableRes backgroundRes: Int) {
    supportDelegate.defaultFragmentBackground = backgroundRes
}

/**
 * 得到位于栈顶Fragment
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportActivity.getTopFragment(): ISupportFragment? {
    return SupportHelper.getTopFragment(supportFragmentManager)
}

/**
 * 获取栈内的fragment对象
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun <T : ISupportFragment> SupportActivity.findFragment(fragmentClass: Class<T>): T? {
    return SupportHelper.findFragment(supportFragmentManager, fragmentClass)
}