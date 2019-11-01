package com.dandanplay.tv.ui.bangumi

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.dandanplay.tv.ui.dialog.SelectMagnetDialogFragment
import com.dandanplay.tv.ui.dialog.setLoadFragment
import com.dandanplay.tv.ui.presenter.SearchMagnetPresenter
import com.dandanplay.tv.vm.SearchMagnetViewModel
import com.seiko.common.ResultData
import com.seiko.common.Status
import com.seiko.common.permission.PermissionResult
import com.seiko.common.permission.requestPermissions
import com.seiko.domain.entity.ResMagnetItem
import org.koin.android.viewmodel.ext.android.viewModel

class SearchMagnetFragment : SearchSupportFragment(),
    SearchSupportFragment.SearchResultProvider,
    SpeechRecognitionCallback,
    OnItemViewClickedListener {

    private val args by navArgs<SearchMagnetFragmentArgs>()

    private val viewModel by viewModel<SearchMagnetViewModel>()

    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // onCreate在onCreateView前，重建View时旧的数据不会往下传递
        viewModel.downloadState.observe(this::getLifecycle, this::updateDownloadUI)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        loadData()
    }

    private fun setupUI() {
        setSearchResultProvider(this)
        setOnItemViewClickedListener(this)
        setSpeechRecognitionCallback(this)
    }

    private fun loadData() {
        viewModel.mainState.observe(this::getLifecycle, this::updateUI)
        if (viewModel.mainState.value == null) {
            requestPermissions(Manifest.permission.RECORD_AUDIO) {
                requestCode = REQUEST_ID_AUDIO
                resultCallback = {
                    when(this) {
                        is PermissionResult.PermissionGranted -> {

                        }
                        else -> {
                            ToastUtils.showShort("没有语音权限。")
                        }
                    }
                }
            }
            setSearchQuery(args.keyword, false)
        }
    }

    /**
     * 加载磁力搜索结果
     */
    private fun updateUI(data: ResultData<List<ResMagnetItem>>) {
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
                updateResults(data.data ?: return)
            }
        }
    }

    /**
     *
     */
    private fun updateDownloadUI(data: ResultData<String>) {
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
                downloadTorrentOver(data.data ?: return)
            }
        }
    }

    private fun updateResults(magnets: List<ResMagnetItem>) {
        val magnetAdapter = ArrayObjectAdapter(SearchMagnetPresenter())
        magnetAdapter.addAll(0, magnets)
        val headerItem = HeaderItem("搜索结果")
        if (rowsAdapter.size() > 0) {
            rowsAdapter.replace(0, ListRow(headerItem, magnetAdapter))
        } else {
            rowsAdapter.add(ListRow(headerItem, magnetAdapter))
        }
    }

    override fun recognizeSpeech() {
        try {
            startActivityForResult(recognizerIntent, REQUEST_SPEECH)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun getResultsAdapter(): ObjectAdapter? {
        return rowsAdapter
    }

    override fun onQueryTextChange(newQuery: String): Boolean {
        search(newQuery.trim())
        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        search(query.trim())
        return true
    }

    private fun search(query: String) {
        if (viewModel.equalQuery(query)) {
            return
        }

        if (query.length < 2) {
            clearSearchResults()
            return
        }

        viewModel.getMagnetListWithSearch(query)
    }

    private fun clearSearchResults() {
        rowsAdapter.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_SPEECH -> {
                when(resultCode) {
                    Activity.RESULT_OK -> {
                        setSearchQuery(data, false)
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 点击磁力
     */
    override fun onItemClicked(holder: Presenter.ViewHolder,
                               item: Any?,
                               rowHolder: RowPresenter.ViewHolder?,
                               row: Row?) {
        when(item) {
            is ResMagnetItem -> {
                requestPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) {
                    requestCode = REQUEST_ID_DOWNLOAD
                    resultCallback = {
                        when(this) {
                            is PermissionResult.PermissionGranted -> {
                                downloadMagnet(item.magnet)
                            }
                            else -> {
                                ToastUtils.showShort("没有存储权限。")
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 下载磁力
     */
    private fun downloadMagnet(magnet: String) {
        val torrentPath = viewModel.isTorrentExist(args.animeTile, magnet)
        if (torrentPath.isNotEmpty()) {
            downloadExisted(torrentPath)
        } else {
            viewModel.downloadTorrent(args.animeTile, magnet)
        }
    }

    /**
     * 种子已下载
     */
    private fun downloadExisted(torrentPath: String) {
        // 暂时直接跳转种子详情
        downloadTorrentOver(torrentPath)
    }

    /**
     * 下载种子完成，进入种子详情页
     */
    private fun downloadTorrentOver(torrentPath: String) {
        findNavController().navigate(
            SearchMagnetFragmentDirections
                .actionEpisodesSearchFragmentToTorrentFileCheckFragment(torrentPath)
        )
    }

    companion object {
        private const val REQUEST_ID_AUDIO = 1122
        private const val REQUEST_ID_DOWNLOAD = 1123

        private const val REQUEST_SPEECH = 2222
    }
}