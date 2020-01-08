package com.seiko.module.torrent.vm

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.blankj.utilcode.util.LogUtils
import com.seiko.common.BaseViewModel
import com.seiko.common.eventbus.EventBusScope
import com.seiko.data.helper.TorrentHelper
import com.seiko.data.usecase.torrent.GetTorrentTempWithContentUseCase
import com.seiko.data.usecase.torrent.GetTorrentTempWithNetUseCase
import com.seiko.domain.utils.Result
import com.seiko.module.torrent.constants.*
import com.seiko.module.torrent.extensions.getLeaves
import com.seiko.module.torrent.extensions.toFileTree
import com.seiko.module.torrent.model.AddTorrentParams
import com.seiko.module.torrent.model.PostEvent
import com.seiko.module.torrent.model.filetree.BencodeFileTree
import com.seiko.module.torrent.ui.fragments.State
import com.seiko.torrent.model.MagnetInfo
import com.seiko.torrent.model.TorrentMetaInfo
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.libtorrent4j.Priority
import java.io.File

class AddTorrentViewModel(
    private val torrentHelper: TorrentHelper,
    private val torrentDownloadDir: File,
    private val getTorrentTempWithContentUseCase: GetTorrentTempWithContentUseCase,
    private val getTorrentTempWithNetUseCase: GetTorrentTempWithNetUseCase
) : BaseViewModel() {

    /**
     * 当前状态
     */
    private val _state = MutableLiveData<Result<Int>>()
    val state: LiveData<Result<Int>> = _state

    /**
     * 磁力信息
     */
    private val _magnetInfo = MutableLiveData<MagnetInfo>()
    val magnetInfo: LiveData<MagnetInfo> = _magnetInfo

    /**
     * 种子信息
     */
    private val _torrentMetaInfo = MutableLiveData<TorrentMetaInfo>()
    val torrentMetaInfo: LiveData<TorrentMetaInfo> = _torrentMetaInfo
    val fileTree: LiveData<BencodeFileTree> = Transformations.map(torrentMetaInfo) { info ->
        info.fileList.toFileTree()
    } // TODO 只有界面加载时fileTree才会处理，待解决

    /**
     * 种子下载路径
     */
    private val _downloadDir = MutableLiveData<File>()
    val downloadDir: LiveData<File> = _downloadDir

    // 自动下载
    var autoStart = true
    // 顺序下载
    var isSequentialDownload = false
    // 自定义名称
    var customName = ""

    private var source = ""
    private var fromMagnet = false

    fun loadData() {
        _downloadDir.value = torrentDownloadDir
        EventBusScope.register(this)
    }

    fun decodeUri(uri: Uri) = launch {
        when(uri.scheme) {
            FILE_PREFIX -> {
                updateState(State.DECODE_TORRENT_FILE)
                source = uri.path!!
                _torrentMetaInfo.value = TorrentMetaInfo(source)
                updateState(State.DECODE_TORRENT_COMPLETED)
            }
            CONTENT_PREFIX -> {
                updateState(State.DECODE_TORRENT_FILE)
                when(val result = getTorrentTempWithContentUseCase.invoke(uri)) {
                    is Result.Success -> {
                        source = result.data
                        _torrentMetaInfo.value = TorrentMetaInfo(source)
                    }
                    is Result.Error -> Result.Error(result.exception)
                }
                updateState(State.DECODE_TORRENT_COMPLETED)
            }
            MAGNET_PREFIX -> {
                fromMagnet = true
                source = uri.toString()
                _magnetInfo.value = torrentHelper.fetchMagnet(source)

                updateState(State.FETCHING_MAGNET)
            }
            HTTP_PREFIX, HTTPS_PREFIX -> {
                updateState(State.FETCHING_HTTP)
                when(val result = getTorrentTempWithNetUseCase.invoke(source)) {
                    is Result.Success -> {
                        source = result.data
                        _torrentMetaInfo.value = TorrentMetaInfo(source)
                    }
                    is Result.Error -> Result.Error(result.exception)
                }
                updateState(State.FETCHING_HTTP_COMPLETED)
            }
            else -> {
                Result.Error(IllegalArgumentException("Unknown link/path type: ${uri.scheme}"))
                return@launch
            }
        }
    }

    private fun updateState(state: Int) {
        _state.value = Result.Success(state)
    }

    /**
     * 创建种子任务
     */
    fun buildTorrentTask(): Result<AddTorrentParams> {

        // 种子解析完成，有信息
        val info = torrentMetaInfo.value ?: return Result.Error(Exception("种子尚未解析完成"))

        // 有有效的存储名称
        val name = if (customName.isNotEmpty()) customName else info.torrentName
        if (name.isEmpty()) {
            return Result.Error(Exception("没有有效的存储名称"))
        }

        // 种子文件数 > 0
        if (info.fileCount == 0) {
            return Result.Error(Exception("种子文件数为0"))
        }

        // 获取需要下载的文件Index
        val fileTree = fileTree.value ?: return Result.Error(Exception("没有有效的文件目录"))
        val selectedIndexes = fileTree.getLeaves().filter { it.isSelected }.map { it.index }
        if (selectedIndexes.isEmpty()) {
            return Result.Error(Exception("没有选中的下载文件"))
        }

        val priorities: List<Priority> = if (info.fileCount == selectedIndexes.size) {
            MutableList(info.fileCount) { Priority.DEFAULT }
        } else {
            MutableList(info.fileCount) { i ->
                if (selectedIndexes.contains(i)) {
                    Priority.DEFAULT
                } else {
                    Priority.IGNORE
                }
            }
        }

        val params = AddTorrentParams(
            source = source,
            fromMagnet = fromMagnet,
            sha1hash = info.sha1Hash,
            name = info.torrentName,
            filePriorities = priorities,
            pathToDownload = _downloadDir.value!!.absolutePath,
            sequentialDownload = isSequentialDownload,
            addPaused = !autoStart)
        return Result.Success(params)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceive(event: PostEvent) {
        when(event) {
            is PostEvent.MetaInfo -> {
               updateState(State.FETCHING_MAGNET_COMPLETED)
                val info = event.info
                LogUtils.d("接收TorrentMetaInfo：${info.torrentName}")
                _torrentMetaInfo.value = info
            }
        }
    }

    override fun onCleared() {
        EventBusScope.unRegister(this)
        super.onCleared()
    }

}