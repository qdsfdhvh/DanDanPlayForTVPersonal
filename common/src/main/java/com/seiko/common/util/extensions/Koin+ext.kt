package com.seiko.common.util.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.koin.getViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

inline fun <reified T : ViewModel> Fragment.parentViewModel(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE) { getParentViewModel(T::class, qualifier, parameters) }

fun <T : ViewModel> Fragment.getParentViewModel(
    clazz: KClass<T>,
    qualifier: Qualifier? = null,
    parameters: ParametersDefinition? = null
): T {
    return getKoin().getViewModel(
        requireParentFragment(),
        clazz,
        qualifier,
        parameters
    )
}