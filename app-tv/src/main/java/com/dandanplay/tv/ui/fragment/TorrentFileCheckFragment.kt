package com.dandanplay.tv.ui.fragment

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
import com.seiko.common.dialog.setLoadFragment
import com.dandanplay.tv.ui.presenter.TorrentFileCheckPresenter
import com.dandanplay.tv.vm.TorrentFileCheckViewModel
import com.seiko.common.ResultData
import com.seiko.common.Status
import com.seiko.common.activity.DispatchKeyEventDispatcherOwner
import com.seiko.common.activity.addCallback
import com.seiko.core.util.TYPE_VIDEO
import com.seiko.core.util.toSingletonList
import com.seiko.core.model.ThunderLocalUrl
import com.seiko.core.model.TorrentCheckBean
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
        viewModel.getTorrentCheckBeanList(args.torrentPath, false)
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
        mAdapter.clear()
        mAdapter.addAll(0, beans)
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
//        com.seiko.player.PlayerManagerActivity.launchPlayerOnline(
//            context = activity!!,
//            videoTitle = thunderLocalUrl.title,
//            videoPath = thunderLocalUrl.url,
//            thunderTaskId = thunderLocalUrl.taskId)
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

}