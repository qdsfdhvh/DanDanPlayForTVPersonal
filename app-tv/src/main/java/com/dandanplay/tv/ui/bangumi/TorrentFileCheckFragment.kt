package com.dandanplay.tv.ui.bangumi

import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_MENU
import android.view.View
import androidx.leanback.widget.*
import androidx.navigation.fragment.navArgs
import com.blankj.utilcode.util.ToastUtils
import androidx.leanback.app.AppVerticalGridFragment
import androidx.leanback.app.AppVerticalGridPresenter
import com.blankj.utilcode.util.LogUtils
import com.dandanplay.tv.ui.dialog.SelectMagnetDialogFragment
import com.dandanplay.tv.ui.dialog.setLoadFragment
import com.dandanplay.tv.ui.player.PlayerManagerActivity
import com.dandanplay.tv.ui.presenter.TorrentFileCheckPresenter
import com.dandanplay.tv.vm.TorrentFileCheckViewModel
import com.seiko.common.ResultData
import com.seiko.common.Status
import com.seiko.common.activity.DispatchKeyEventDispatcherOwner
import com.seiko.common.activity.addCallback
import com.seiko.data.utils.TYPE_VIDEO
import com.seiko.data.utils.toSingletonList
import com.seiko.domain.entity.ThunderLocalUrl
import com.seiko.domain.entity.TorrentCheckBean
import org.koin.android.viewmodel.ext.android.viewModel

class TorrentFileCheckFragment : AppVerticalGridFragment(), OnItemViewClickedListener {

    private val args by navArgs<TorrentFileCheckFragmentArgs>()

    private val viewModel by viewModel<TorrentFileCheckViewModel>()

    private lateinit var mAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.thunderUrl.observe(this::getLifecycle, this::updateThunderUrl)
        setupUI()
        setupGridPresenter()
        setupRowAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
    }

    private fun setupUI() {
        title = "种子详情"
        showTitle(true)
        onItemViewClickedListener = this
        (requireActivity() as? DispatchKeyEventDispatcherOwner)
            ?.getDispatchKeyEventDispatcher()
            ?.addCallback(this, true, this::dispatchKeyEvent)
    }

    private fun setupGridPresenter() {
        if (gridPresenter != null) return
        val presenter = AppVerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_SMALL)
        presenter.numberOfColumns = 1
        setGridPresenter(presenter)
    }

    private fun setupRowAdapter() {
        if(adapter != null) return
        val presenter = TorrentFileCheckPresenter()
        mAdapter = ArrayObjectAdapter(presenter)
        adapter = mAdapter
    }

    private fun loadData() {
        viewModel.mainState.observe(this::getLifecycle, this::updateUI)
        if (viewModel.mainState.value == null) {
            viewModel.getTorrentCheckBeanList(args.torrentPath)
        }
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
//        val presenter = TorrentFileCheckPresenter()
//        val mAdapter = ArrayObjectAdapter(presenter)
//        mAdapter.addAll(0, beans)
//        adapter = mAdapter
        mAdapter.clear()
        mAdapter.addAll(0, beans)
//        prepareEntranceTransition()
    }

    private fun updateThunderUrl(data: ResultData<ThunderLocalUrl>) {
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
                launchPlayerOnline(data.data ?: return)
            }
        }
    }

    private fun launchPlayerOnline(thunderLocalUrl: ThunderLocalUrl) {
        PlayerManagerActivity.launchPlayerOnline(
            context = activity!!,
            videoTitle = thunderLocalUrl.title,
            videoPath = thunderLocalUrl.url,
            thunderTaskId = thunderLocalUrl.taskId)
    }

    override fun onItemClicked(holder: Presenter.ViewHolder?,
                               item: Any?,
                               rowHolder: RowPresenter.ViewHolder?,
                               row: Row?) {
        when(item) {
            is TorrentCheckBean -> {
                if (childFragmentManager.findFragmentByTag(SelectMagnetDialogFragment.TAG) == null) {
                    SelectMagnetDialogFragment.Builder()
                        .isVideo(item.type == TYPE_VIDEO)
                        .setOnDownloadClickListener {
                            getTorrentTask(item)
                        }
                        .setOnPlayClickListener {
                            playForThunder(item)
                        }
                        .build()
                        .show(childFragmentManager)
                }
            }
        }
    }

    private fun getTorrentTask(item: TorrentCheckBean) {
        item.isChecked = true
        viewModel.getTorrentTask(args.torrentPath, item.toSingletonList())
    }

    private fun playForThunder(item: TorrentCheckBean) {
        viewModel.playForThunder(args.torrentPath, item)
    }

    private fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            LogUtils.d("keyCode = ${event.keyCode}")
            when(event.keyCode) {
                KEYCODE_MENU -> {
                    ToastUtils.showShort("点击菜单键")
                }
            }
        }
        return false
    }


//    /**
//     * thunder在线播放
//     */
//    private fun playForThunder(checkedFilePosition: Int,
//                               checkedFileSize: Long,
//                               torrentFilePath: String) {
//
//        val thunderTorrentInfo = XLTaskHelper.getInstance().getTorrentInfo(torrentFilePath)
//        if (thunderTorrentInfo == null) {
//            ToastUtils.showShort("播放失败，无法解析播放内容")
//            return
//        }
//
//        val cacheFolder = File(DEFAULT_CACHE_FOLDER_PATH)
//        if (!cacheFolder.exists() && !cacheFolder.mkdirs()) {
//            ToastUtils.showShort("播放失败，创建缓存文件夹失败")
//            return
//        }
//        FileUtils.deleteAllInDir(cacheFolder)
//
//        if (cacheFolder.freeSpace < checkedFileSize) {
//            ToastUtils.showShort("播放失败，剩余缓存空间不足")
//            return
//        }
//
//        // 构建参数
//        val taskParam = BtTaskParam()
//        taskParam.setCreateMode(1)
//        taskParam.setFilePath(cacheFolder.absolutePath)
//        taskParam.setMaxConcurrent(atomicInteger.incrementAndGet())
//        taskParam.setSeqId(1)
//        taskParam.setTorrentPath(torrentFilePath)
//
//        // 选择的文件
//        val selectIndexSet = BtIndexSet(1)
//        selectIndexSet.mIndexSet[0] = checkedFilePosition
//
//        // 忽略的文件
//        val size = thunderTorrentInfo.mSubFileInfo.size
//        val deSelectIndexSet = BtIndexSet(size - 1)
//        var j = 0
//        for (i in 0 until size) {
//            if (i != checkedFilePosition) {
//                deSelectIndexSet.mIndexSet[j++] = 0
//            }
//        }
//
//        // 开启任务
//        val playTaskId = XLTaskHelper.getInstance().startTask(taskParam, selectIndexSet, deSelectIndexSet)
//
//
//        // 任务出错重试
//        if (playTaskId == -1L) {
//            XLTaskHelper.stopTask(playTaskId)
//            if (atomicInteger.get() < 3) {
//                XLDownloadManager.getInstance().uninit()
//                XLTaskHelper.init(activity!!.applicationContext, 2)
//                playForThunder(checkedFilePosition, checkedFileSize, torrentFilePath)
//            } else {
//                FileUtils.deleteAllInDir(DEFAULT_CACHE_FOLDER_PATH)
//                ToastUtils.showShort("播放失败，无法开始播放任务")
//            }
//            return
//        }
//
//        val fileName = thunderTorrentInfo.mSubFileInfo[checkedFilePosition].mFileName
//        val filePath = taskParam.mFilePath + File.separator + fileName
//        val localUrl = XLTaskLocalUrl()
//        XLDownloadManager.getInstance().getLocalUrl(filePath, localUrl)
//        LogUtils.d("fileName = $fileName, filePath = $filePath, localUrl = ${localUrl.mStrUrl}")
//
//        PlayerManagerActivity.launchPlayerOnline(
//            context = activity!!,
//            videoTitle = fileName,
//            videoPath = localUrl.mStrUrl,
//            thunderTaskId = playTaskId)
//    }

}