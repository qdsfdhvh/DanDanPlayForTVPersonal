package com.seiko.common.util.timber

import timber.log.Timber
import java.util.*

class NanoDebugTree: Timber.DebugTree() {

    override fun createStackElementTag(element: StackTraceElement): String? {
        return String.format(Locale.getDefault(), "(%s:%d)", element.fileName, +element.lineNumber)
    }

}