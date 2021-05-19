package com.seiko.tv.ui.search

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.*
import com.github.fragivity.navigator
import com.github.fragivity.push
import com.seiko.common.router.Navigator
import com.seiko.common.ui.adapter.AsyncObjectAdapter
import com.seiko.tv.data.db.model.ResMagnetItemEntity
import com.seiko.tv.data.model.api.SearchAnimeDetails
import com.seiko.tv.ui.bangumi.BangumiDetailsFragment
import com.seiko.tv.ui.widget.presenter.BangumiPresenterSelector
import com.seiko.tv.util.diff.SearchAnimeDetailsDiffCallback
import com.seiko.tv.vm.SearchBangumiViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class SearchBangumiFragment : SearchSupportFragment(),
    SearchSupportFragment.SearchResultProvider,
    SpeechRecognitionCallback,
    OnItemViewClickedListener {

    companion object {
        private const val ROW_BANGUMI = 100
        private const val ROW_MAGNET = 200

        private const val REQUEST_SPEECH = 2222

        fun newInstance(): SearchBangumiFragment {
            return SearchBangumiFragment()
        }
    }

    private val viewModel: SearchBangumiViewModel by viewModels()

    @Inject
    lateinit var presenterSelector: BangumiPresenterSelector

    private lateinit var rowsAdapter: ArrayObjectAdapter
    private lateinit var bangumiAdapter: AsyncObjectAdapter<SearchAnimeDetails>
    private lateinit var magnetAdapter: ArrayObjectAdapter

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

        bangumiAdapter = AsyncObjectAdapter(presenterSelector, SearchAnimeDetailsDiffCallback())
        createListRow(ROW_BANGUMI, "相关作品", bangumiAdapter)

        magnetAdapter = ArrayObjectAdapter(presenterSelector)
        createListRow(ROW_MAGNET, "磁力链接", magnetAdapter)
    }

    private fun createListRow(id: Int, title: String, objectAdapter: ObjectAdapter) {
        val headerItem = HeaderItem(id.toLong(), title)
        val listRow = ListRow(headerItem, objectAdapter)
        rowsAdapter.add(listRow)
    }

    private fun bindViewModel() {
        viewModel.bangumiList.observe(viewLifecycleOwner, bangumiAdapter::submitList)
        viewModel.magnetList.observe(viewLifecycleOwner) { list ->
            magnetAdapter.setItems(list, null)
        }
    }

    override fun recognizeSpeech() {
        try {
            startActivityForResult(
                recognizerIntent,
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
        when (item) {
            is SearchAnimeDetails -> {
                navigator.push {
                    BangumiDetailsFragment.newInstance(item.animeId, item.imageUrl)
                }
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
        Navigator.navToAddTorrent(this, uri)
    }

    /**
     * Activity退栈回调
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_SPEECH -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        setSearchQuery(data, true)
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}