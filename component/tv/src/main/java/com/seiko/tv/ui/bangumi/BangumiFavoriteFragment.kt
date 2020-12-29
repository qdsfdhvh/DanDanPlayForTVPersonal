package com.seiko.tv.ui.bangumi

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.*
import androidx.paging.LoadState
import com.seiko.common.ui.adapter.AsyncPagedObjectAdapter
import com.seiko.common.util.extensions.viewLifecycleScope
import com.seiko.tv.R
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.ui.card.MainAreaCardView
import com.seiko.tv.ui.presenter.BangumiPresenterSelector
import com.seiko.tv.ui.presenter.SpacingVerticalGridPresenter
import com.seiko.tv.util.diff.HomeImageBeanDiffCallback
import com.seiko.tv.vm.BangumiFavoriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BangumiFavoriteFragment : VerticalGridSupportFragment(), OnItemViewClickedListener {

    companion object {
        private const val COLUMNS = 5

        fun newInstance(): BangumiFavoriteFragment {
            return BangumiFavoriteFragment()
        }
    }

    private val viewModel: BangumiFavoriteViewModel by viewModels()

    @Inject
    lateinit var presenterSelector: BangumiPresenterSelector

    private lateinit var arrayAdapter: AsyncPagedObjectAdapter<HomeImageBean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRowAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun setupRowAdapter() {
        val verticalGridPresenter =
            SpacingVerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM, false)
        verticalGridPresenter.numberOfColumns = COLUMNS
        verticalGridPresenter.setItemSpacing(40)

        onItemViewClickedListener = this
        gridPresenter = verticalGridPresenter

        arrayAdapter = AsyncPagedObjectAdapter(presenterSelector, HomeImageBeanDiffCallback())
        adapter = arrayAdapter

        prepareEntranceTransition()
    }

    private fun bindViewModel() {
        viewModel.bangumiCount.observe(viewLifecycleOwner) {
            title = "%s (%d)".format(getString(R.string.bangumi_favorite), it)
        }
        viewLifecycleScope.launch {
            arrayAdapter.loadStateFlow.collectLatest { loadStates ->
                if (loadStates.refresh !is LoadState.Loading) {
                    delay(200)
                    startEntranceTransition()
                }
            }
        }
        viewModel.loadData()
            .onEach { arrayAdapter.submitData(it) }
            .launchIn(viewLifecycleScope)
    }

    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {
        when (item) {
            is HomeImageBean -> {
                val cardView = itemViewHolder.view as MainAreaCardView
                BangumiDetailsActivity.launch(requireActivity(), item, cardView.getImageView())
            }
        }
    }


}