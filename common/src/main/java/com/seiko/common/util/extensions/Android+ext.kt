package com.seiko.common.util.extensions

/**
 * Faster lazy delegation for Android.
 * Warning: Only use for objects accessed on main thread
 */
fun <T> lazyAndroid(initializer: () -> T): Lazy<T>
        = lazy(LazyThreadSafetyMode.NONE, initializer)