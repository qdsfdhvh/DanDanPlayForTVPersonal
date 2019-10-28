package com.dandanplay.tv.ui.bangumi

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.ToastUtils
import com.dandanplay.tv.ui.dialog.setLoadFragment
import com.dandanplay.tv.ui.presenter.SearchBangumiPresenter
import com.dandanplay.tv.vm.SearchBangumiViewModel
import com.seiko.common.ResultData
import com.seiko.common.Status
import com.seiko.common.permission.PermissionResult
import com.seiko.common.permission.requestPermissions
import com.seiko.domain.entity.SearchAnimeDetails
import org.koin.android.viewmodel.ext.android.viewModel

class SearchBangumiFragment : SearchSupportFragment(),
    SearchSupportFragment.SearchResultProvider,
    SpeechRecognitionCallback,
    OnItemViewClickedListener {

    private val viewModel by viewModel<SearchBangumiViewModel>()

    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            // 测试
            setSearchQuery("勇者", false)
            search("勇者")
        }
    }

    private fun updateUI(data: ResultData<List<SearchAnimeDetails>>) {
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

    private fun updateResults(results: List<SearchAnimeDetails>) {
        val bangumiAdapter = ArrayObjectAdapter(SearchBangumiPresenter())
        val headerItem = HeaderItem("搜索结果")
        bangumiAdapter.addAll(0, results)
        val row = ListRow(headerItem, bangumiAdapter)
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

        viewModel.getBangumiListWithSearch(query)
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
            is SearchAnimeDetails -> {
                findNavController().navigate(
                    SearchBangumiFragmentDirections.actionSearchBangumiFragmentToBangumiDetailsFragment(item.animeId)
                )
            }
        }
    }

    companion object {
        private const val REQUEST_ID_AUDIO = 1122

        private const val REQUEST_SPEECH = 2222
    }

}