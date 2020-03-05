package com.seiko.tv.ui.search

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.observe
import com.seiko.tv.vm.SearchMagnetViewModel
import com.seiko.common.router.Navigator
import com.seiko.common.router.Routes
import com.seiko.common.ui.adapter.AsyncObjectAdapter
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.common.util.toast.toast
import com.seiko.tv.data.db.model.ResMagnetItemEntity
import com.seiko.tv.ui.presenter.BangumiPresenterSelector
import com.seiko.tv.util.diff.ResMagnetItemDiffCallback
import org.koin.android.viewmodel.ext.android.viewModel

class SearchMagnetFragment : SearchSupportFragment(),
    SearchSupportFragment.SearchResultProvider,
    SpeechRecognitionCallback,
    OnItemViewClickedListener {

    companion object {
        const val ARGS_KEYWORD = "ARGS_KEYWORD"
        const val ARGS_ANIME_ID = "ARGS_ANIME_ID"
        const val ARGS_EPISODE_ID = "ARGS_EPISODE_ID"
        private const val ROW_MAGNET = 200

        private const val REQUEST_ID_AUDIO = 1122

        private const val REQUEST_SPEECH = 2222
        private const val REQUEST_TORRENT = 2223

        fun newInstance(bundle: Bundle): SearchMagnetFragment {
            val fragment = SearchMagnetFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

//    private val args by navArgs<SearchMagnetFragmentArgs>()
    private val keyword by lazyAndroid { arguments!!.getString(ARGS_KEYWORD)!! }
    private val animeId by lazyAndroid { arguments!!.getLong(ARGS_ANIME_ID) }
    private val episodeId by lazyAndroid { arguments!!.getInt(ARGS_EPISODE_ID) }

    private val viewModel by viewModel<SearchMagnetViewModel>()

    private lateinit var rowsAdapter: ArrayObjectAdapter
    private lateinit var magnetAdapter: AsyncObjectAdapter<ResMagnetItemEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupRows()
        setSearchQuery(keyword, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unBindViewModel()
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

    private fun bindViewModel() {
        viewModel.magnetList.observe(this) { magnets ->
            magnetAdapter.submitList(magnets)
        }
    }

    private fun unBindViewModel() {
        viewModel.magnetList.removeObservers(this)
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
        Navigator.navToAddTorrent(this, uri,
            REQUEST_TORRENT
        )
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
                            viewModel.saveMagnetInfoUseCase(hash, animeId, episodeId)
                        }
                    }
                }
            }
        }
    }

}