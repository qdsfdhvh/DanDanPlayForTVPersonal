package com.seiko.tv.ui.fragment

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.findNavController
import com.seiko.common.ui.dialog.setLoadFragment
import com.seiko.tv.ui.presenter.SearchBangumiPresenter
import com.seiko.tv.ui.presenter.SearchMagnetPresenter
import com.seiko.tv.data.model.AnimeRow
import com.seiko.tv.ui.card.SearchMagnetCardView
import com.seiko.tv.util.diff.SearchAnimeDetailsDiffCallback
import com.seiko.tv.vm.SearchBangumiViewModel
import com.seiko.common.data.ResultData
import com.seiko.common.router.Navigator
import com.seiko.common.router.Routes
import com.seiko.common.util.toast.toast
import com.seiko.tv.data.db.model.ResMagnetItemEntity
import com.seiko.tv.data.model.api.SearchAnimeDetails
import com.seiko.tv.ui.presenter.BangumiPresenterSelector
import com.seiko.tv.util.diff.ResMagnetItemDiffCallback
import org.koin.android.viewmodel.ext.android.viewModel

class SearchBangumiFragment : SearchSupportFragment(),
    SearchSupportFragment.SearchResultProvider,
    SpeechRecognitionCallback,
    OnItemViewClickedListener {

    companion object {
        private const val ROW_BANGUMI = 100
        private const val ROW_MAGNET = 200

        private const val REQUEST_SPEECH = 2222
        private const val REQUEST_TORRENT = 2223
    }

    private val viewModel by viewModel<SearchBangumiViewModel>()

    private lateinit var rowsAdapter: ArrayObjectAdapter
    private lateinit var arrayAdapters: SparseArray<ArrayObjectAdapter>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupRows()
        // 测试
        setSearchQuery("紫罗兰", true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun setupUI() {
        setSearchResultProvider(this)
        setOnItemViewClickedListener(this)
        setSpeechRecognitionCallback(this)
    }

    private fun setupRows() {
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        arrayAdapters = SparseArray(2)
        createListRow(ROW_BANGUMI, "相关作品")
        createListRow(ROW_MAGNET, "磁力链接")

    }

    private fun createListRow(id: Int, title: String) {
        val presenterSelector = BangumiPresenterSelector()
        val headerItem = HeaderItem(id.toLong(), title)
        val objectAdapter = ArrayObjectAdapter(presenterSelector)
        val listRow = ListRow(headerItem, objectAdapter)
        rowsAdapter.add(listRow)
        arrayAdapters.put(id, objectAdapter)
    }

    private fun bindViewModel() {
        viewModel.bangumiList.observe(this::getLifecycle, this::updateBangumiList)
        viewModel.magnetList.observe(this::getLifecycle, this::updateMagnetList)
    }

    private fun updateBangumiList(results: List<SearchAnimeDetails>) {
        arrayAdapters[ROW_BANGUMI]?.setItems(results, SearchAnimeDetailsDiffCallback())
    }

    private fun updateMagnetList(results: List<ResMagnetItemEntity>) {
        arrayAdapters[ROW_MAGNET]?.setItems(results, ResMagnetItemDiffCallback())
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
     * 点击：动画 / 磁力
     */
    override fun onItemClicked(
        holder: Presenter.ViewHolder,
       item: Any?,
       rowHolder: RowPresenter.ViewHolder?,
       row: Row?
    ) {
        when(item) {
            is SearchAnimeDetails -> {
                findNavController().navigate(
                    SearchBangumiFragmentDirections.actionSearchBangumiFragmentToBangumiDetailsFragment(
                        item.animeId
                    )
                )
            }
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

}