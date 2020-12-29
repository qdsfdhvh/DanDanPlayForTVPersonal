package com.seiko.common.util.extensions

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Page分页类装为Flow
 */
fun <T : Any> PagingSource<Int, T>.asFlow(pageSize: Int = 10): Flow<PagingData<T>> {
    return Pager(
        config = PagingConfig(
            pageSize = pageSize,
            enablePlaceholders = false
        ),
    ) { this }.flow
}

fun <T : Any, R : Any> Flow<PagingData<T>>.dataMap(transform: suspend (T) -> R): Flow<PagingData<R>> {
    return map { it.map(transform) }
}