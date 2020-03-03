package com.seiko.common.util.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

@Suppress("FunctionName")
fun IOScope(): CoroutineScope = object : CoroutineScope {
    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.IO
    override fun toString(): String = "CoroutineScope(coroutineContext=$coroutineContext)"
}
