package com.seiko.player.ui.helper

import android.os.Handler
import java.lang.ref.WeakReference


abstract class WeakHandler<T>(owner: T) : Handler() {

    private val mOwner: WeakReference<T> = WeakReference(owner)

    val owner: T? get() = mOwner.get()

}