package com.seiko.common.util.autosize.callback

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * ================================================
 * {@link FragmentLifecycleCallbacksImplToAndroidx} 可用来代替在 BaseFragment 中加入适配代码的传统方式
 * {@link FragmentLifecycleCallbacksImplToAndroidx} 这种方案类似于 AOP, 面向接口, 侵入性低, 方便统一管理, 扩展性强, 并且也支持适配三方库的 {@link Fragment}
 * <p>
 * Created by JessYan on 2018/8/25 13:52
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
internal class FragmentLifecycleCallbacksImpl(
    var autoAdaptStrategy: AutoAdaptStrategy
) : FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        autoAdaptStrategy.apply(f, f.requireActivity())
    }

}