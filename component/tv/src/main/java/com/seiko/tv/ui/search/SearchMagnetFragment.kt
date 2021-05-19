package com.seiko.tv.ui.search

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.*
import com.seiko.common.router.Navigator
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.common.util.toast.toast
import com.seiko.tv.data.db.model.ResMagnetItemEntity
import com.seiko.tv.ui.widget.presenter.BangumiPresenterSelector
import com.seiko.tv.vm.SearchMagnetViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class SearchMagnetFragment : SearchSupportFragment(),
    SearchSupportFragment.SearchResultProvider,
    SpeechRecognitionCallback,
    OnItemViewClickedListener {

    companion object {
        private const val ARGS_KEYWORD = "ARGS_KEYWORD"

        private const val ROW_MAGNET = 200
        private const val REQUEST_ID_AUDIO = 1122
        private const val REQUEST_SPEECH = 2222

        fun newInstance(keyword: String): SearchMagnetFragment {
            return SearchMagnetFragment().apply {
                arguments = bundleOf(ARGS_KEYWORD to keyword)
            }
        }
    }

    private val keyword by lazyAndroid { requireArguments().getString(ARGS_KEYWORD)!! }

    private val viewModel: SearchMagnetViewModel by viewModels()

    @Inject
    lateinit var presenterSelector: BangumiPresenterSelector

    private lateinit var rowsAdapter: ArrayObjectAdapter
    private lateinit var magnetAdapter: ArrayObjectAdapter

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

    private fun setupUI() {
        setSearchResultProvider(this)
        setOnItemViewClickedListener(this)
        setSpeechRecognitionCallback(this)
    }

    private fun setupRows() {
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

        magnetAdapter = ArrayObjectAdapter(presenterSelector)
        createListRow(ROW_MAGNET, "磁力链接", magnetAdapter)
    }

    private fun createListRow(id: Int, title: String, objectAdapter: ObjectAdapter) {
        val headerItem = HeaderItem(id.toLong(), title)
        val listRow = ListRow(headerItem, objectAdapter)
        rowsAdapter.add(listRow)
    }

    private fun bindViewModel() {
        viewModel.magnetList.observe(viewLifecycleOwner) { magnets ->
            magnetAdapter.setItems(magnets, null)
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
     * 点击：磁力
     */
    override fun onItemClicked(
        holder: Presenter.ViewHolder,
        item: Any?,
        rowHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {
        when (item) {
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
     * 权限请求回调
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
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
        when (requestCode) {
            REQUEST_SPEECH -> {
                if (resultCode == Activity.RESULT_OK) {
                    setSearchQuery(data, false)
                }
            }
        }
    }

}