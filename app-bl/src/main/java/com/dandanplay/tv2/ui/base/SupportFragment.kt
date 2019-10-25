package com.dandanplay.tv2.ui.base

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import androidx.fragment.app.Fragment
import com.seiko.common.lazyAndroid
import me.yokeyword.fragmentation.*
import me.yokeyword.fragmentation.anim.FragmentAnimator

abstract class SupportFragment: Fragment(), ISupportFragment {

    protected lateinit var mActivity: SupportActivity

    private val delegate by lazyAndroid { SupportFragmentDelegate(this) }

    override fun getSupportDelegate(): SupportFragmentDelegate {
        return delegate
    }

    override fun extraTransaction(): ExtraTransaction {
        return extraTransaction()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SupportActivity) {
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

/**
 * 隐藏软键盘
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportFragment.hideSoftInput() {
    supportDelegate.hideSoftInput()
}

/**
 * 显示软键盘,调用该方法后,会在onPause时自动隐藏软键盘
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportFragment.showSoftInput(view: View) {
    supportDelegate.showSoftInput(view)
}

/**
 * 加载根Fragment, 即Activity内的第一个Fragment 或 Fragment内的第一个子Fragment
 *
 * @param containerId 容器id
 * @param toFragment  目标Fragment
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportFragment.loadRootFragment(containerId: Int,
                                                                       toFragment: ISupportFragment) {
    supportDelegate.loadRootFragment(containerId, toFragment)
}

@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportFragment.loadRootFragment(containerId: Int,
                                                                       toFragment: ISupportFragment,
                                                                       addToBackStack: Boolean) {
    supportDelegate.loadRootFragment(containerId, toFragment, addToBackStack, false)
}

@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportFragment.loadRootFragment(containerId: Int,
                                                                       toFragment: ISupportFragment,
                                                                       addToBackStack: Boolean,
                                                                       allowAnimation: Boolean) {
    supportDelegate.loadRootFragment(containerId, toFragment, addToBackStack, allowAnimation)
}

/**
 * 加载多个同级根Fragment,类似Wechat, QQ主页的场景
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportFragment.loadMultipleRootFragment(containerId: Int,
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
inline fun SupportFragment.showHideFragment(show: ISupportFragment) {
    supportDelegate.showHideFragment(show)
}

@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportFragment.showHideFragment(show: ISupportFragment, hide: ISupportFragment?) {
    supportDelegate.showHideFragment(show, hide)
}


/**
 * It is recommended to use {@link SupportFragment#start(ISupportFragment)}.
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportFragment.start(toFragment: ISupportFragment) {
    supportDelegate.start(toFragment)
}

@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportFragment.start(toFragment: ISupportFragment,
                                                            @ISupportFragment.LaunchMode launchModel: Int) {
    supportDelegate.start(toFragment, launchModel)
}

/**
 * It is recommended to use {@link SupportFragment#startForResult(ISupportFragment, int)}.
 * Launch an fragment for which you would like a result when it poped.
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportFragment.startForResult(toFragment: ISupportFragment, requestCode: Int) {
    supportDelegate.startForResult(toFragment, requestCode)
}

/**
 * It is recommended to use {@link SupportFragment#startWithPop(ISupportFragment)}.
 * Start the target Fragment and pop itself
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportFragment.startWithPop(toFragment: ISupportFragment) {
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
inline fun SupportFragment.startWithPopTo(toFragment: ISupportFragment,
                                                                     targetFragmentClass: Class<*>,
                                                                     includeTargetFragment: Boolean) {
    supportDelegate.startWithPopTo(toFragment, targetFragmentClass, includeTargetFragment)
}

/**
 * It is recommended to use {@link SupportFragment#replaceFragment(ISupportFragment, boolean)}.
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportFragment.replaceFragment(toFragment: ISupportFragment, addToBackStack: Boolean) {
    supportDelegate.replaceFragment(toFragment, addToBackStack)
}

/**
 * Pop the fragment.
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportFragment.pop() {
    supportDelegate.pop()
}

/**
 * Pop the child fragment.
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportFragment.popChild() {
    supportDelegate.popChild()
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
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportFragment.popTo(targetFragmentClass: Class<*>,
                                                            includeTargetFragment: Boolean) {
    supportDelegate.popTo(targetFragmentClass, includeTargetFragment)
}

@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportFragment.popTo(targetFragmentClass: Class<*>,
                                                            includeTargetFragment: Boolean,
                                                            afterPopTransactionRunnable: Runnable) {
    supportDelegate.popTo(targetFragmentClass, includeTargetFragment,
        afterPopTransactionRunnable)
}

@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportFragment.popTo(targetFragmentClass: Class<*>,
                                                            includeTargetFragment: Boolean,
                                                            afterPopTransactionRunnable: Runnable?,
                                                            popAnim: Int) {
    supportDelegate.popTo(targetFragmentClass, includeTargetFragment,
        afterPopTransactionRunnable, popAnim)
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
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportFragment.popToChild(targetFragmentClass: Class<*>,
                                                                 includeTargetFragment: Boolean) {
    supportDelegate.popToChild(targetFragmentClass, includeTargetFragment)
}

@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportFragment.popToChild(targetFragmentClass: Class<*>,
                                                                 includeTargetFragment: Boolean,
                                                                 afterPopTransactionRunnable: Runnable) {
    supportDelegate.popToChild(targetFragmentClass, includeTargetFragment,
        afterPopTransactionRunnable)
}

@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportFragment.popToChild(targetFragmentClass: Class<*>,
                                                                 includeTargetFragment: Boolean,
                                                                 afterPopTransactionRunnable: Runnable?,
                                                                 popAnim: Int) {
    supportDelegate.popToChild(targetFragmentClass, includeTargetFragment,
        afterPopTransactionRunnable, popAnim)
}

/**
 * 得到位于栈顶Fragment
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportFragment.getTopFragment(): ISupportFragment? {
    return SupportHelper.getTopFragment(fragmentManager)
}

/**
 * 得到位于子栈顶Fragment
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportFragment.getTopChildFragment(): ISupportFragment? {
    return SupportHelper.getTopFragment(childFragmentManager)
}

/**
 * @return 位于当前Fragment的前一个Fragment
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportFragment.getPreFragment(): ISupportFragment? {
    return SupportHelper.getPreFragment(this)
}

/**
 * 获取栈内的fragment对象
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun <T : ISupportFragment> SupportFragment.findFragment(fragmentClass: Class<T>): T? {
    return SupportHelper.findFragment(fragmentManager, fragmentClass)
}

/**
 * 获取栈内的fragment对象
 */
@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun <T : ISupportFragment> SupportFragment.findChildFragment(fragmentClass: Class<T>): T? {
    return SupportHelper.findFragment(childFragmentManager, fragmentClass)
}

@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportFragment.activityStart(toFragment: ISupportFragment) {
    val act = activity as? SupportActivity ?: return
    act.start(toFragment)
}
//
//@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
//inline fun SupportFragment.activityStartForResult(toFragment: ISupportFragment, requestCode: Int) {
//    val act = activity as? SupportActivity ?: return
//    act.startForResult(toFragment, requestCode)
//}

@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
inline fun SupportFragment.activitySupport(block: SupportActivity.() -> Unit) {
    val act = activity as? SupportActivity ?: return
    act.run(block)
}