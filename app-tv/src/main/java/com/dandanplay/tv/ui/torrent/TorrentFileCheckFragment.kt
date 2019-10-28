package com.dandanplay.tv.ui.torrent

import android.os.Bundle
import android.view.View
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.navArgs
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.dandanplay.tv.ui.dialog.setLoadFragment
import com.dandanplay.tv.ui.player.PlayerManagerActivity
import com.dandanplay.tv.ui.presenter.TorrentFileCheckPresenter
import com.dandanplay.tv.vm.TorrentFileCheckViewModel
import com.seiko.common.ResultData
import com.seiko.common.Status
import com.seiko.data.utils.DEFAULT_CACHE_FOLDER_PATH
import com.seiko.data.utils.TYPE_VIDEO
import com.seiko.domain.entity.TorrentCheckBean
import com.xunlei.downloadlib.XLDownloadManager
import com.xunlei.downloadlib.XLTaskHelper
import com.xunlei.downloadlib.parameter.BtIndexSet
import com.xunlei.downloadlib.parameter.BtTaskParam
import com.xunlei.downloadlib.parameter.XLTaskLocalUrl
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

class TorrentFileCheckFragment : VerticalGridSupportFragment(), OnItemViewClickedListener {

    private val args by navArgs<TorrentFileCheckFragmentArgs>()

    private val viewModel by viewModel<TorrentFileCheckViewModel>()

    private val atomicInteger = AtomicInteger(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRowAdapter()
        onItemViewClickedListener = this
        title = "种子详情"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.mainState.observe(this::getLifecycle, this::updateUI)
        if (viewModel.mainState.value == null) {
            viewModel.getTorrentCheckBeanList(args.torrentPath)
        }
    }

    private fun setupRowAdapter() {
        val gridPresenter = VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM)
        gridPresenter.numberOfColumns = 1
        setGridPresenter(gridPresenter)
    }

    private fun updateUI(data: ResultData<List<TorrentCheckBean>>) {
        when(data.responseType) {
            Status.LOADING -> {
                setLoadFragment(true)
            }
            Status.ERROR -> {
                setLoadFragment(false)
                ToastUtils.showShort(data.error.toString())
            }
            Status.SUCCESSFUL -> {
                setLoadFragment(false)
                updateTorrentCheckUI(data.data ?: return)
            }
        }
    }

    private fun updateTorrentCheckUI(beans: List<TorrentCheckBean>) {
        val presenter = TorrentFileCheckPresenter()
        val mAdapter = ArrayObjectAdapter(presenter)
        mAdapter.addAll(0, beans)
        adapter = mAdapter
    }

    override fun onItemClicked(holder: Presenter.ViewHolder?,
                               item: Any?,
                               rowHolder: RowPresenter.ViewHolder?,
                               row: Row?) {
        when(item) {
            is TorrentCheckBean -> {
                if (item.type != TYPE_VIDEO) {
                    ToastUtils.showShort("不是可播放的视频文件")
                    return
                }
                atomicInteger.set(0)
                playForThunder(item.index, item.size, args.torrentPath)
            }
        }
    }

    /**
     * thunder在线播放
     */
    private fun playForThunder(checkedFilePosition: Int,
                               checkedFileSize: Long,
                               torrentFilePath: String) {

        val thunderTorrentInfo = XLTaskHelper.getInstance().getTorrentInfo(torrentFilePath)
        if (thunderTorrentInfo == null) {
            ToastUtils.showShort("播放失败，无法解析播放内容")
            return
        }

        val cacheFolder = File(DEFAULT_CACHE_FOLDER_PATH)
        if (!cacheFolder.exists() && !cacheFolder.mkdirs()) {
            ToastUtils.showShort("播放失败，创建缓存文件夹失败")
            return
        }
        FileUtils.deleteAllInDir(cacheFolder)

        if (cacheFolder.freeSpace < checkedFileSize) {
            ToastUtils.showShort("播放失败，剩余缓存空间不足")
            return
        }

        // 构建参数
        val taskParam = BtTaskParam()
        taskParam.setCreateMode(1)
        taskParam.setFilePath(cacheFolder.absolutePath)
        taskParam.setMaxConcurrent(3)
        taskParam.setSeqId(atomicInteger.incrementAndGet())
        taskParam.setTorrentPath(torrentFilePath)

        // 选择的文件
        val selectIndexSet = BtIndexSet(1)
        selectIndexSet.mIndexSet[0] = checkedFilePosition

        // 忽略的文件
        val size = thunderTorrentInfo.mSubFileInfo.size
        val deSelectIndexSet = BtIndexSet(size - 1)
        var j = 0
        for (i in 0 until size) {
            if (i != checkedFilePosition) {
                deSelectIndexSet.mIndexSet[j++] = 0
            }
        }

        // 开启任务
        val playTaskId = XLTaskHelper.getInstance().startTask(taskParam, selectIndexSet, deSelectIndexSet)


        // 任务出错重试
        if (playTaskId == -1L) {
            XLTaskHelper.stopTask(playTaskId)
            if (atomicInteger.get() < 3) {
                XLDownloadManager.getInstance().uninit()
                XLTaskHelper.init(activity!!.applicationContext, 2)
                playForThunder(checkedFilePosition, checkedFileSize, torrentFilePath)
            } else {
                FileUtils.deleteAllInDir(DEFAULT_CACHE_FOLDER_PATH)
                ToastUtils.showShort("播放失败，无法开始播放任务")
            }
            return
        }

        val fileName = thunderTorrentInfo.mSubFileInfo[checkedFilePosition].mFileName
        val filePath = taskParam.mFilePath + File.separator + fileName
        val localUrl = XLTaskLocalUrl()
        XLDownloadManager.getInstance().getLocalUrl(filePath, localUrl)
        LogUtils.d("fileName = $fileName, filePath = $filePath, localUrl = ${localUrl.mStrUrl}")

        PlayerManagerActivity.launchPlayerOnline(
            context = activity!!,
            videoTitle = fileName,
            videoPath = localUrl.mStrUrl,
            thunderTaskId = playTaskId)
    }

}