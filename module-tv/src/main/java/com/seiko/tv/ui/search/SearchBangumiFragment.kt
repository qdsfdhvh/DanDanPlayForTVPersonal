package com.seiko.tv.ui.search

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.*
import com.seiko.tv.util.diff.SearchAnimeDetailsDiffCallback
import com.seiko.tv.vm.SearchBangumiViewModel
import com.seiko.common.router.Navigator
import com.seiko.common.router.Routes
import com.seiko.common.ui.adapter.AsyncObjectAdapter
import com.seiko.tv.data.db.model.ResMagnetItemEntity
import com.seiko.tv.data.model.api.SearchAnimeDetails
import com.seiko.tv.ui.bangumi.BangumiDetailsActivity
import com.seiko.tv.ui.card.SearchBangumiCardView
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

        fun newInstance(): SearchBangumiFragment {
            return SearchBangumiFragment()
        }
    }

    private val viewModel by viewModel<SearchBangumiViewModel>()

    private lateinit var rowsAdapter: ArrayObjectAdapter
    private lateinit var bangumiAdapter: AsyncObjectAdapter<SearchAnimeDetails>
    private lateinit var magnetAdapter: AsyncObjectAdapter<ResMagnetItemEntity>

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
        val presenterSelector = BangumiPresenterSelector()

        bangumiAdapter = AsyncObjectAdapter(presenterSelector, SearchAnimeDetailsDiffCallback())
        createListRow(ROW_BANGUMI, "相关作品", bangumiAdapter)

        magnetAdapter = AsyncObjectAdapter(presenterSelector, ResMagnetItemDiffCallback())
        createListRow(ROW_MAGNET, "磁力链接", magnetAdapter)
    }

    private fun createListRow(id: Int, title: String, objectAdapter: ObjectAdapter) {
        val headerItem = HeaderItem(id.toLong(), title)
        val listRow = ListRow(headerItem, objectAdapter)
        rowsAdapter.add(listRow)
    }

    private fun bindViewModel() {
        viewModel.bangumiList.observe(this::getLifecycle, bangumiAdapter::submitList)
        viewModel.magnetList.observe(this::getLifecycle, magnetAdapter::submitList)
    }

    override fun recognizeSpeech() {
        try {
            startActivityForResult(recognizerIntent,
                REQUEST_SPEECH
            )
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
                val cardView = holder.view as SearchBangumiCardView
                BangumiDetailsActivity.launch(requireActivity(), item.animeId, cardView.getImageView())
//                findNavController().navigate(
//                    SearchBangumiFragmentDirections.actionSearchBangumiFragmentToBangumiDetailsFragment(
//                        item.animeId
//                    )
//                )
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
        Navigator.navToAddTorrent(this, uri,
            REQUEST_TORRENT
        )
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