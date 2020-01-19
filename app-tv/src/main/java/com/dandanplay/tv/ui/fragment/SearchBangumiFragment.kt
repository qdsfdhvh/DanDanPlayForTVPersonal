package com.dandanplay.tv.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.findNavController
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ToastUtils
import com.seiko.common.dialog.setLoadFragment
import com.dandanplay.tv.ui.presenter.SearchBangumiPresenter
import com.dandanplay.tv.ui.presenter.SearchMagnetPresenter
import com.dandanplay.tv.model.AnimeRow
import com.dandanplay.tv.ui.card.SearchMagnetCardView
import com.dandanplay.tv.vm.SearchBangumiViewModel
import com.seiko.common.ResultData
import com.seiko.common.Status
import com.seiko.common.extensions.checkPermissions
import com.seiko.common.router.Navigator
import com.seiko.common.router.Routes
import com.seiko.core.data.db.model.ResMagnetItemEntity
import com.seiko.core.model.api.SearchAnimeDetails
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File

class SearchBangumiFragment : SearchSupportFragment(),
    SearchSupportFragment.SearchResultProvider,
    SpeechRecognitionCallback,
    OnItemViewClickedListener {

    private val viewModel by viewModel<SearchBangumiViewModel>()

    private var rowsAdapter: ArrayObjectAdapter? = null // ArrayObjectAdapter(ListRowPresenter())

    private lateinit var adapterRows: SparseArray<AnimeRow<*>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRows()
        bindViewModel()
    }

    private fun setupUI() {
        setSearchResultProvider(this)
        setOnItemViewClickedListener(this)
        setSpeechRecognitionCallback(this)
    }

    private fun setupRows() {
        if (rowsAdapter != null) return

        adapterRows = SparseArray(2)
        adapterRows.put(
            ROW_BANGUMI, AnimeRow<SearchAnimeDetails>(ROW_BANGUMI)
                .setAdapter(SearchBangumiPresenter())
                .setTitle("相关作品"))
        adapterRows.put(
            ROW_MAGNET, AnimeRow<SearchMagnetCardView>(ROW_MAGNET)
                .setAdapter(SearchMagnetPresenter())
                .setTitle("磁力链接"))

        // 生成界面的Adapter
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        for (i in 0 until adapterRows.size()) {
            val row = adapterRows.valueAt(i)
            val headerItem = HeaderItem(row.getId(), row.getTitle())
            val listRow = ListRow(headerItem, row.getAdapter())
            rowsAdapter.add(listRow)
        }
        this.rowsAdapter = rowsAdapter
    }

    private fun bindViewModel() {
        viewModel.mainState.observe(this::getLifecycle, this::updateUI)
        viewModel.bangumiList.observe(this::getLifecycle, this::updateBangumiList)
        viewModel.magnetList.observe(this::getLifecycle, this::updateMagnetList)

        if (viewModel.mainState.value == null) {
            if (!checkPermissions(PERMISSIONS_AUDIO)) {
                requestPermissions(PERMISSIONS_AUDIO, REQUEST_ID_AUDIO)
            }

            // 测试
            setSearchQuery("紫罗兰花园", false)
            search("紫罗兰花园")
        }
    }

    private fun updateUI(data: ResultData<Boolean>) {
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
            }
        }
    }

    private fun updateBangumiList(results: List<SearchAnimeDetails>) {
        adapterRows[ROW_BANGUMI]?.setList(results)
    }

    private fun updateMagnetList(results: List<ResMagnetItemEntity>) {
        adapterRows[ROW_MAGNET]?.setList(results)
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
//        search(newQuery.trim())
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

        viewModel.getBangumiListAndMagnetList(query)
    }

    private fun clearSearchResults() {
//        rowsAdapter.clear()
    }

    /**
     * 点击：动画 / 磁力
     */
    override fun onItemClicked(holder: Presenter.ViewHolder,
                               item: Any?,
                               rowHolder: RowPresenter.ViewHolder?,
                               row: Row?) {
        when(item) {
            is SearchAnimeDetails -> {
                findNavController().navigate(
                    SearchBangumiFragmentDirections.actionSearchBangumiFragmentToBangumiDetailsFragment(item.animeId)
                )
            }
            is ResMagnetItemEntity -> {
                viewModel.setCurrentMagnetItem(item)
                if (checkPermissions(PERMISSIONS_DOWNLOAD)) {
                    downloadMagnet()
                } else {
                    requestPermissions(PERMISSIONS_DOWNLOAD, REQUEST_ID_DOWNLOAD)
                }
            }
        }
    }

    /**
     * 跳转Torrent下载种子
     */
    private fun downloadMagnet() {
        val uri = viewModel.getCurrentMagnetUri() ?: return
        Navigator.navToTorrent(this, uri, REQUEST_TORRENT)
    }

    /**
     * 权限请求回调
     */
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQUEST_ID_AUDIO -> {
                if (!grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    ToastUtils.showShort("没有语音权限。")
                }
            }
            REQUEST_ID_DOWNLOAD -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    downloadMagnet()
                } else {
                    ToastUtils.showShort("没有存储权限。")
                }
            }
        }
    }

    /**
     * Activity退栈回调
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_SPEECH -> {
                when(resultCode) {
                    Activity.RESULT_OK -> {
                        setSearchQuery(data, true)
                    }
                }
            }
            REQUEST_TORRENT -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val success = data.getBooleanExtra(Routes.Torrent.RESULT_KEY_ADD_SUCCESS, false)
                    if (success) {
                        val hash = data.getStringExtra(Routes.Torrent.RESULT_KEY_ADD_HASH)
                        if (hash != null) {
                            // 这里不存在animeId与episodeId，都是-1
                            viewModel.saveMagnetInfoUseCase(hash, -1, -1)
                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val ROW_BANGUMI = 100
        private const val ROW_MAGNET = 200

        private const val REQUEST_ID_AUDIO = 1122
        private const val REQUEST_ID_DOWNLOAD = 1123

        private const val REQUEST_SPEECH = 2222
        private const val REQUEST_TORRENT = 2223

        private val PERMISSIONS_AUDIO = arrayOf(
            Manifest.permission.RECORD_AUDIO
        )

        private val PERMISSIONS_DOWNLOAD = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

}