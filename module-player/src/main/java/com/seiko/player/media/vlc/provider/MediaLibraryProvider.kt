package com.seiko.player.media.vlc.provider

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.Config
import androidx.paging.DataSource
import androidx.paging.PositionalDataSource
import androidx.paging.toLiveData
import com.seiko.player.media.vlc.interfaces.SortModule
import com.seiko.player.media.vlc.util.MEDIA_LIBRARY_PAGE_SIZE
import com.seiko.player.media.vlc.util.helper.ModelsHelper
import kotlinx.coroutines.CompletableDeferred
import org.videolan.medialibrary.interfaces.Medialibrary
import org.videolan.medialibrary.media.MediaLibraryItem

abstract class MediaLibraryProvider<T : MediaLibraryItem>(
    val context: Context,
    val model: SortableModel
) : HeaderProvider(), SortModule {

    protected val mediaLibrary by lazy { Medialibrary.getInstance() }

    private lateinit var dataSource: DataSource<Int, T>

    val loading = MutableLiveData<Boolean>().apply { value = true }

    private var refreshDeferred : CompletableDeferred<Unit>? = null

    var isRefreshing = mediaLibrary.isWorking
        private set(value) {
            refreshDeferred = if (value) CompletableDeferred()
            else {
                refreshDeferred?.complete(Unit)
                null
            }
            loading.postValue(value || mediaLibrary.isWorking)
            field = value
        }

    protected open val sortKey : String = "MediaLibraryProvider"

    var sort = Medialibrary.SORT_DEFAULT
    var desc = false

    open fun isByDisc(): Boolean = false

    override fun sort(sort: Int) {
        if (canSortBy(sort)) {
            desc = when (this.sort) {
                Medialibrary.SORT_DEFAULT -> sort == Medialibrary.SORT_ALPHA
                sort -> !desc
                else -> false
            }
            this.sort = sort
            refresh()
        }
    }

    suspend fun awaitRefresh() {
        refresh()
        refreshDeferred?.await()
    }

    fun refresh(): Boolean {
        if (isRefreshing || !mediaLibrary.isStarted || !this::dataSource.isInitialized) return false
        privateHeaders.clear()
        if (!dataSource.isInvalid) {
            isRefreshing = true
            dataSource.invalidate()
        }
        return true
    }

    private val pagingConfig = Config(
        pageSize = MEDIA_LIBRARY_PAGE_SIZE,
        prefetchDistance = MEDIA_LIBRARY_PAGE_SIZE / 5,
        enablePlaceholders = true,
        initialLoadSizeHint = MEDIA_LIBRARY_PAGE_SIZE,
        maxSize = MEDIA_LIBRARY_PAGE_SIZE * 2)

    val pagedList = MLDataSourceFactory().toLiveData(pagingConfig)

    fun isEmpty() = pagedList.value.isNullOrEmpty()

    fun completeHeaders(list: Array<T>, startPosition: Int) {
        for ((position, item) in list.withIndex()) {
            val previous = when {
                position > 0 -> list[position - 1]
                startPosition > 0 -> pagedList.value?.getOrNull(startPosition + position - 1)
                else -> null
            }
            ModelsHelper.getHeader(
                context,
                sort,
                item,
                previous,
                isByDisc()
            )?.let {
                privateHeaders.put(startPosition + position, it)
            }
        }
        (liveHeaders as MutableLiveData).postValue(privateHeaders.clone())
    }

    inner class MLDataSource : PositionalDataSource<T>() {
        override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<T>) {
            val page = getPage(params.requestedLoadSize, params.requestedStartPosition)
            val count = if (page.size < params.requestedLoadSize) page.size else getTotalCount()
            try {
                callback.onResult(page.toList(), params.requestedStartPosition, count)
            } catch (e: IllegalArgumentException) {}
            isRefreshing = false
        }

        override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<T>) {
            val result = getPage(params.loadSize, params.startPosition).toList()
            callback.onResult(result)
        }
    }

    inner class MLDataSourceFactory : DataSource.Factory<Int, T>() {
        override fun create() = MLDataSource().also { dataSource = it }
    }

    abstract fun getTotalCount(): Int
    abstract fun getPage(loadSize: Int, startPosition: Int): Array<T>
    abstract fun getAll(): Array<T>
}