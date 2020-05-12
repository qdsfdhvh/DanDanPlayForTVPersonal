package com.seiko.tv.ui.bangumi

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.observe
import androidx.lifecycle.lifecycleScope
import com.seiko.common.ui.adapter.AsyncObjectAdapter
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.tv.R
import com.seiko.tv.data.db.model.BangumiEpisodeEntity
import com.seiko.tv.data.model.BangumiDetailBean
import com.seiko.tv.data.model.EpisodesListRow
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.data.model.RelatesListRow
import com.seiko.tv.ui.card.MainAreaCardView
import com.seiko.tv.ui.presenter.BangumiPresenterSelector
import com.seiko.tv.ui.presenter.CustomFullWidthDetailsOverviewRowPresenter
import com.seiko.tv.ui.presenter.DetailsDescriptionPresenter
import com.seiko.tv.ui.presenter.FrescoDetailsOverviewLogoPresenter
import com.seiko.tv.ui.search.SearchActivity
import com.seiko.tv.util.diff.HomeImageBeanDiffCallback
import com.seiko.tv.vm.BangumiDetailViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class BangumiDetailsFragment : DetailsSupportFragment()
    , OnItemViewClickedListener
    , OnActionClickedListener {

    companion object {
        const val TAG = "BangumiDetailsFragment"
        const val ARGS_ANIME_ID = "ARGS_ANIME_ID"
        const val TRANSITION_NAME = "t_for_transition";
        private const val ID_RATING = 1L
        private const val ID_FAVOURITE = 2L

        fun newInstance(bundle: Bundle): BangumiDetailsFragment {
            val fragment = BangumiDetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private val animeId: Long by lazyAndroid { requireArguments().getLong(ARGS_ANIME_ID) }
    private val viewModel: BangumiDetailViewModel by viewModel()

    private lateinit var mPresenterSelector: ClassPresenterSelector
    private lateinit var mAdapter: ArrayObjectAdapter
    private lateinit var mActionAdapter: ArrayObjectAdapter
    private lateinit var mDescriptionRowPresenter: CustomFullWidthDetailsOverviewRowPresenter

    private lateinit var detailsOverviewRow: AppDetailsOverviewRow
    private lateinit var episodesAdapter: ArrayObjectAdapter
    private lateinit var relatedListRow: ListRow
    private lateinit var relatedAdapter: AsyncObjectAdapter<HomeImageBean>
    private lateinit var similarListRow: ListRow
    private lateinit var similarAdapter: AsyncObjectAdapter<HomeImageBean>

    private val homeImageBeanDiffCallback by lazyAndroid { HomeImageBeanDiffCallback() }

    private var mDetailsOverviewPrevState = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        bindViewModel()
        onItemViewClickedListener = this
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onItemViewClickedListener = null
        mDetailsOverviewPrevState = mDescriptionRowPresenter.mPreviousState
    }

    /**
     * 生成相关UI
     */
    private fun setupUI() {
        // diy listRow确保哈希不同
        mPresenterSelector = ClassPresenterSelector()
        mPresenterSelector.addClassPresenter(EpisodesListRow::class.java, ListRowPresenter())
        mPresenterSelector.addClassPresenter(RelatesListRow::class.java, ListRowPresenter())
        mPresenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())
        mAdapter = ArrayObjectAdapter(mPresenterSelector)
        adapter = mAdapter
        setupListRow()
        prepareEntranceTransition()
    }

    /**
     * 添加简介、集数、其他。相似
     */
    private fun setupListRow() {
        val logoPresenter = FrescoDetailsOverviewLogoPresenter()
        val descriptionPresenter = DetailsDescriptionPresenter()
        mDescriptionRowPresenter = CustomFullWidthDetailsOverviewRowPresenter(descriptionPresenter, logoPresenter)
        mDescriptionRowPresenter.setViewHolderState(mDetailsOverviewPrevState)
        mDescriptionRowPresenter.onActionClickedListener = this@BangumiDetailsFragment
        mDescriptionRowPresenter.isParticipatingEntranceTransition = false

        val mHelper = FullWidthDetailsOverviewSharedElementHelper()
        mHelper.setSharedElementEnterTransition(requireActivity(), TRANSITION_NAME)
        mDescriptionRowPresenter.setListener(mHelper)
        mDescriptionRowPresenter.isParticipatingEntranceTransition = false

        mPresenterSelector.addClassPresenter(DetailsOverviewRow::class.java, mDescriptionRowPresenter)

        detailsOverviewRow = AppDetailsOverviewRow(BangumiDetailBean.empty())
        detailsOverviewRow.setImageBitmap(requireContext(), null) // 需要此行，原因尚未确定
        detailsOverviewRow.isImageScaleUpAllowed = true
        mAdapter.add(detailsOverviewRow)

        val presenterSelector = BangumiPresenterSelector()

        episodesAdapter = ArrayObjectAdapter(presenterSelector)
        mAdapter.add(EpisodesListRow(HeaderItem(0, "分集"), episodesAdapter))

        relatedAdapter = AsyncObjectAdapter(presenterSelector, homeImageBeanDiffCallback)
        relatedListRow = RelatesListRow(HeaderItem(0, "其他系列"), relatedAdapter)

        similarAdapter = AsyncObjectAdapter(presenterSelector, homeImageBeanDiffCallback)
        similarListRow = ListRow(HeaderItem(0, "相似作品"), similarAdapter)
    }


    /**
     * 开始加载数据
     */
    private fun bindViewModel() {
        viewModel.bangumiDetailsBean.observe(viewLifecycleOwner) { details ->
            updateDetailsOverviewRow(details)
            startEntranceTransition()
        }
        viewModel.episodesList.observe(viewLifecycleOwner) { episodes ->
            episodesAdapter.setItems(episodes, null)
        }
        viewModel.relatedsList.observe(viewLifecycleOwner) { relateds ->
            mAdapter.showHideListList(relatedListRow, relateds)
            relatedAdapter.submitList(relateds)
        }
        viewModel.similarsList.observe(viewLifecycleOwner) { similars ->
            mAdapter.showHideListList(similarListRow, similars)
            similarAdapter.submitList(similars)
        }
        viewModel.animeId.value = animeId
    }

    /**
     * 添加简介布局数据
     */
    private fun updateDetailsOverviewRow(details: BangumiDetailBean) {
        if (details.overviewRowBackgroundColor != 0) {
            mDescriptionRowPresenter.backgroundColor = details.overviewRowBackgroundColor
        }
        if (details.actionBackgroundColor != 0) {
            mDescriptionRowPresenter.actionsBackgroundColor = details.actionBackgroundColor
        }

        // TODO 待优化
        detailsOverviewRow.item = details
        detailsOverviewRow.notifyImageDrawable()

        mActionAdapter = ArrayObjectAdapter()
        mActionAdapter.add(
            Action(ID_RATING, "评分:${details.rating}", null,
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_rating))
        )
        mActionAdapter.add(
            if (details.isFavorited) {
                Action(ID_FAVOURITE, "已收藏", null,
                    ContextCompat.getDrawable(requireActivity(), R.drawable.ic_heart_full))
            } else {
                Action(ID_FAVOURITE, "未收藏", null,
                    ContextCompat.getDrawable(requireActivity(), R.drawable.ic_heart_empty))
            }
        )
        detailsOverviewRow.actionsAdapter = mActionAdapter
    }

    /**
     * 点击：收藏
     */
    override fun onActionClicked(action: Action?) {
        when(action?.id) {
            ID_FAVOURITE -> {
                lifecycleScope.launch {
                    if (viewModel.setFavourite()) {
                        Timber.d("已收藏")
                        action.label1 = "已收藏"
                        action.icon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_heart_full)
                    } else {
                        Timber.d("未收藏")
                        action.label1 = "未收藏"
                        action.icon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_heart_empty)
                    }
                    val index = mActionAdapter.indexOf(action)
                    mActionAdapter.notifyItemRangeChanged(index, 1)
                }
            }
        }
    }

    /**
     * 点击：集数、相关工作
     */
    override fun onItemClicked(holder: Presenter.ViewHolder, item: Any?,
                               rowHolder: RowPresenter.ViewHolder?, row: Row?) {
        when(item) {
            is BangumiEpisodeEntity -> {
                val keyword = viewModel.getSearchKey(item)
                SearchActivity.launchMagnet(requireActivity(), keyword, animeId, item.episodeId)
            }
            is HomeImageBean -> {
                val card = holder.view
                card as MainAreaCardView
                BangumiDetailsActivity.launch(requireActivity(), item.animeId, card.getImageView())
            }
        }
    }

}

private fun <T> ArrayObjectAdapter.showHideListList(listRow: ListRow, list: Collection<T>) {
    if (list.isEmpty()) {
        if (indexOf(listRow) >= 0) {
            remove(listRow)
        }
    } else {
        if (indexOf(listRow) < 0) {
            add(listRow)
        }
    }
}