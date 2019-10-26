package com.dandanplay.tv.ui.bangumi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.navArgs
import com.blankj.utilcode.util.ToastUtils
import com.dandanplay.tv.ui.dialog.setLoadFragment
import com.dandanplay.tv.ui.presenter.SearchMagnetPresenter
import com.dandanplay.tv.vm.EpisodesSearchViewModel
import com.seiko.common.ResultData
import com.seiko.common.Status
import com.seiko.domain.entity.ResMagnetItem
import org.koin.android.viewmodel.ext.android.viewModel

class EpisodesSearchFragment : SearchSupportFragment(), SearchSupportFragment.SearchResultProvider,
    OnItemViewClickedListener {

    private val args by navArgs<EpisodesSearchFragmentArgs>()

    private val viewModel by viewModel<EpisodesSearchViewModel>()

    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
    private lateinit var magnetAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSearchResultProvider(this)
        setOnItemViewClickedListener(this)
        magnetAdapter = ArrayObjectAdapter(SearchMagnetPresenter())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.mainState.observe(this::getLifecycle, this::updateUI)
        checkPermissions()
        if (viewModel.mainState.value == null) {
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

    private fun updateResults(magnets: List<ResMagnetItem>) {
//        if (rowsAdapter.size() > 0) {
//            rowsAdapter.removeItems(0, 1)
//        }
//        val objectAdapter = ArrayObjectAdapter(SearchMagnetPresenter())
//        objectAdapter.addAll(0, magnets)
//        val headerItem = HeaderItem("搜索结果")
//        rowsAdapter.add(ListRow(headerItem, objectAdapter))
        magnetAdapter.clear()
        val headerItem = HeaderItem("搜索结果")
        magnetAdapter.addAll(0, magnets)
        val row = ListRow(headerItem, magnetAdapter)
        rowsAdapter.clear()
        rowsAdapter.add(row)

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

    override fun onItemClicked(holder: Presenter.ViewHolder,
                               item: Any?,
                               rowHolder: RowPresenter.ViewHolder?,
                               row: Row?) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            REQUEST_CODE_RECORD_AUDIO -> {
//                if (resultCode == RESULT_OK) {
//                    setSearchQuery(data, true)
//                }
            }
        }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            val granted = ContextCompat.checkSelfPermission(activity!!, Manifest.permission.RECORD_AUDIO)
            if (granted != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_CODE_RECORD_AUDIO
                )
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_RECORD_AUDIO = 1234
    }
}