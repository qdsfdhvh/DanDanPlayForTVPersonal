package com.seiko.tv.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.navArgs
import com.seiko.tv.ui.presenter.SearchMagnetPresenter
import com.seiko.tv.vm.SearchMagnetViewModel
import com.seiko.common.data.ResultData
import com.seiko.common.util.extensions.checkPermissions
import com.seiko.common.router.Navigator
import com.seiko.common.router.Routes
import com.seiko.common.ui.adapter.AsyncObjectAdapter
import com.seiko.common.util.toast.toast
import com.seiko.tv.data.db.model.ResMagnetItemEntity
import com.seiko.tv.ui.presenter.BangumiPresenterSelector
import com.seiko.tv.util.diff.ResMagnetItemDiffCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class SearchMagnetFragment : SearchSupportFragment(),
    SearchSupportFragment.SearchResultProvider,
    SpeechRecognitionCallback,
    OnItemViewClickedListener {

    companion object {
        private const val ROW_MAGNET = 200

        private const val REQUEST_ID_AUDIO = 1122

        private const val REQUEST_SPEECH = 2222
        private const val REQUEST_TORRENT = 2223
    }

    private val args by navArgs<SearchMagnetFragmentArgs>()

    private val viewModel by viewModel<SearchMagnetViewModel>()

    private lateinit var rowsAdapter: ArrayObjectAdapter
    private lateinit var magnetAdapter: AsyncObjectAdapter<ResMagnetItemEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupRows()
        setSearchQuery(args.keyword, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
    }

    private fun setupUI() {
        setSearchResultProvider(this)
        setOnItemViewClickedListener(this)
        setSpeechRecognitionCallback(this)
    }

    private fun setupRows() {
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val presenterSelector = BangumiPresenterSelector()

        magnetAdapter = AsyncObjectAdapter(presenterSelector, ResMagnetItemDiffCallback())
        createListRow(ROW_MAGNET, "磁力链接", magnetAdapter)
    }

    private fun createListRow(id: Int, title: String, objectAdapter: ObjectAdapter) {
        val headerItem = HeaderItem(id.toLong(), title)
        val listRow = ListRow(headerItem, objectAdapter)
        rowsAdapter.add(listRow)
    }

    private fun loadData() {
        viewModel.magnetList.observe(this::getLifecycle) { magnets ->
            magnetAdapter.submitList(magnets) {
                if (magnets.isNotEmpty()) {
                    Timber.d("重置焦点")
                    rowsSupportFragment.setSelectedPosition(0, false)
                }
            }
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
        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        viewModel.keyword.value = query.trim()
        return true
    }

    /**
     * 点击：磁力
     */
    override fun onItemClicked(
        holder: Presenter.ViewHolder,
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
        Navigator.navToAddTorrent(this, uri, REQUEST_TORRENT)
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
                    toast("没有语音权限。")
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

}