package com.dandanplay.tv.ui.bangumi

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.dandanplay.tv.ui.dialog.setLoadFragment
import com.dandanplay.tv.ui.presenter.SearchMagnetPresenter
import com.dandanplay.tv.vm.SearchEpisodesViewModel
import com.seiko.common.ResultData
import com.seiko.common.Status
import com.seiko.common.permission.PermissionResult
import com.seiko.common.permission.requestPermissions
import com.seiko.domain.entity.ResMagnetItem
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.atomic.AtomicInteger

class SearchEpisodesFragment : SearchSupportFragment(),
    SearchSupportFragment.SearchResultProvider,
    SpeechRecognitionCallback,
    OnItemViewClickedListener {

    private val args by navArgs<SearchEpisodesFragmentArgs>()

    private val viewModel by viewModel<SearchEpisodesViewModel>()

    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.downloadState.observe(this::getLifecycle, this::updateDownloadUI)
        setSearchResultProvider(this)
        setOnItemViewClickedListener(this)
        setSpeechRecognitionCallback(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        val headerItem = HeaderItem("搜索结果")
        magnetAdapter.addAll(0, magnets)
        val row = ListRow(headerItem, magnetAdapter)
        rowsAdapter.clear()
        rowsAdapter.add(row)
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
                        setSearchQuery(data, true)
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

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
     * 本地存在此磁力的种子
     */
    private fun downloadExisted(torrentPath: String) {
//        ToastUtils.showShort("种子已存在。")
        downloadTorrentOver(torrentPath)
    }

    /**
     * 下载种子完成，进入种子详情页
     */
    private fun downloadTorrentOver(torrentPath: String) {
        LogUtils.d("torrentPath = $torrentPath")
        findNavController().navigate(
            SearchEpisodesFragmentDirections.actionEpisodesSearchFragmentToTorrentFileCheckFragment(torrentPath)
        )
    }

    companion object {
        private const val REQUEST_ID_AUDIO = 1122
        private const val REQUEST_ID_DOWNLOAD = 1123

        private const val REQUEST_SPEECH = 2222
    }
}