package com.dandanplay.tv.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.navArgs
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.seiko.common.ui.dialog.setLoadFragment
import com.dandanplay.tv.ui.presenter.SearchMagnetPresenter
import com.dandanplay.tv.vm.SearchMagnetViewModel
import com.seiko.common.ResultData
import com.seiko.common.Status
import com.seiko.common.extensions.checkPermissions
import com.seiko.common.router.Navigator
import com.seiko.common.router.Routes
import com.seiko.core.data.db.model.ResMagnetItemEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import org.koin.android.viewmodel.ext.android.viewModel

class SearchMagnetFragment : SearchSupportFragment(), CoroutineScope by MainScope(),
    SearchSupportFragment.SearchResultProvider,
    SpeechRecognitionCallback,
    OnItemViewClickedListener {

    private val args by navArgs<SearchMagnetFragmentArgs>()

    private val viewModel by viewModel<SearchMagnetViewModel>()

    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }

    private fun setupUI() {
        setSearchResultProvider(this)
        setOnItemViewClickedListener(this)
        setSpeechRecognitionCallback(this)
    }

    private fun loadData() {
        viewModel.mainState.observe(this::getLifecycle, this::updateUI)
        if (viewModel.mainState.value == null) {
            if (!checkPermissions(PERMISSIONS_AUDIO)) {
                requestPermissions(PERMISSIONS_AUDIO, REQUEST_ID_AUDIO)
            }
            setSearchQuery(args.keyword, false)
        }
    }

    /**
     * 加载磁力搜索结果
     */
    private fun updateUI(data: ResultData<List<ResMagnetItemEntity>>) {
        when(data.responseType) {
            Status.LOADING -> {
                setLoadFragment(true)
            }
            Status.ERROR -> {
                setLoadFragment(false)
                LogUtils.d(data.error)
                ToastUtils.showShort(data.error.toString())
            }
            Status.SUCCESSFUL -> {
                setLoadFragment(false)
                updateResults(data.data ?: return)
            }
        }
    }

    private fun updateResults(magnets: List<ResMagnetItemEntity>) {
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

    /**
     * 点击：磁力
     */
    override fun onItemClicked(holder: Presenter.ViewHolder,
                               item: Any?,
                               rowHolder: RowPresenter.ViewHolder?,
                               row: Row?
    ) {
        when(item) {
            is ResMagnetItemEntity -> {
                viewModel.setCurrentMagnetItem(item)
                downloadMagnet()
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
        }
    }

    /**
     * Activity退栈回调
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_SPEECH -> {
                if (resultCode == Activity.RESULT_OK) {
                    setSearchQuery(data, false)
                }
            }
            REQUEST_TORRENT -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val success = data.getBooleanExtra(Routes.Torrent.RESULT_KEY_ADD_SUCCESS, false)
                    if (success) {
                        val hash = data.getStringExtra(Routes.Torrent.RESULT_KEY_ADD_HASH)
                        if (hash != null) {
                            viewModel.saveMagnetInfoUseCase(hash, args.animeId, args.episodeId)
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val REQUEST_ID_AUDIO = 1122

        private const val REQUEST_SPEECH = 2222
        private const val REQUEST_TORRENT = 2223

        private val PERMISSIONS_AUDIO = arrayOf(
            Manifest.permission.RECORD_AUDIO
        )

    }
}