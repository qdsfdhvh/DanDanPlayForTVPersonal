package com.seiko.tv.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.seiko.common.util.makeSceneTransitionAnimation
import com.seiko.tv.R
import com.seiko.tv.data.db.model.BangumiEpisodeEntity
import com.seiko.tv.data.model.BangumiDetailBean
import com.seiko.tv.data.model.EpisodesListRow
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.ui.card.MainAreaCardView
import com.seiko.tv.ui.presenter.*
import com.seiko.tv.vm.BangumiDetailViewModel
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class BangumiDetailsFragment : DetailsSupportFragment()
    , OnItemViewClickedListener
    , OnActionClickedListener {

    companion object {
        private const val ID_RATING = 1L
        private const val ID_FAVOURITE = 2L
    }

    private val args by navArgs<BangumiDetailsFragmentArgs>()
    private val viewModel by viewModel<BangumiDetailViewModel>()

    private lateinit var mPresenterSelector: ClassPresenterSelector
    private lateinit var mAdapter: ArrayObjectAdapter
    private lateinit var mActionAdapter: ArrayObjectAdapter
    private lateinit var mDescriptionRowPresenter: CustomFullWidthDetailsOverviewRowPresenter

    private var mDetailsOverviewPrevState = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        bindViewModel()
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
        mPresenterSelector = ClassPresenterSelector()
        mAdapter = ArrayObjectAdapter(mPresenterSelector)
        onItemViewClickedListener = this
        createDetailsOverviewRowPresenter()

        prepareEntranceTransition()

        adapter = mAdapter
    }

    /**
     * 开始加载数据
     */
    private fun bindViewModel() {
        viewModel.bangumiDetailsBean.observe(this::getLifecycle, this::updateDetails)
        viewModel.animeId.value = args.animeId
    }

    /**
     * 更新动漫详情
     */
    private fun updateDetails(details: BangumiDetailBean) {
        createDetailsOverviewRow(details)
        createEpisodesRows(details.episodes)
        createRelatedsRows(details.relateds)
        createSimilarsRows(details.similars)
        startEntranceTransition()
    }

    /**
     * 添加简介布局选择器
     */
    private fun createDetailsOverviewRowPresenter() {
        val logoPresenter = FrescoDetailsOverviewLogoPresenter()
        val descriptionPresenter = DetailsDescriptionPresenter()
        mDescriptionRowPresenter = CustomFullWidthDetailsOverviewRowPresenter(descriptionPresenter, logoPresenter)
        mDescriptionRowPresenter.setViewHolderState(mDetailsOverviewPrevState)
        mDescriptionRowPresenter.onActionClickedListener = this@BangumiDetailsFragment
        mPresenterSelector.addClassPresenter(DetailsOverviewRow::class.java, mDescriptionRowPresenter)

        mDescriptionRowPresenter.isParticipatingEntranceTransition = false
        prepareEntranceTransition()
    }

    /**
     * 添加简介布局数据
     */
    private fun createDetailsOverviewRow(details: BangumiDetailBean) {
        if (details.overviewRowBackgroundColor != 0) {
            mDescriptionRowPresenter.backgroundColor = details.overviewRowBackgroundColor
        }
        if (details.actionBackgroundColor != 0) {
            mDescriptionRowPresenter.actionsBackgroundColor = details.actionBackgroundColor
        }

        val detailsOverviewRow = DetailsOverviewRow(details)
        detailsOverviewRow.setImageBitmap(requireActivity(), null)
        detailsOverviewRow.isImageScaleUpAllowed = true
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
        mAdapter.add(detailsOverviewRow)
    }

    /**
     * 添加 分集
     */
    private fun createEpisodesRows(episodes: List<BangumiEpisodeEntity>) {
        if (episodes.isNotEmpty()) {
            val presenterSelector = BangumiPresenterSelector()
            val episodesAdapter = ArrayObjectAdapter(presenterSelector)
            episodesAdapter.addAll(0, episodes)
            val header = HeaderItem(0, "分集")
            mAdapter.add(EpisodesListRow(header, episodesAdapter))
            mPresenterSelector.addClassPresenter(EpisodesListRow::class.java, ListRowPresenter())
        }
    }

    /**
     * 添加 其他系列
     */
    private fun createRelatedsRows(relateds: List<HomeImageBean>) {
        if (relateds.isNotEmpty()) {
            val presenterSelector = BangumiPresenterSelector()
            val relatedAdapter = ArrayObjectAdapter(presenterSelector)
            relatedAdapter.addAll(0, relateds)
            val header = HeaderItem(0, "其他系列")
            mAdapter.add(ListRow(header, relatedAdapter))
            mPresenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())
        }
    }

    /**
     * 添加 相似作品
     */
    private fun createSimilarsRows(similars: List<HomeImageBean>) {
        if (similars.isNotEmpty()) {
            val presenterSelector = BangumiPresenterSelector()
            val relatedAdapter = ArrayObjectAdapter(presenterSelector)
            relatedAdapter.addAll(0, similars)
            val header = HeaderItem(0, "相似作品")
            mAdapter.add(ListRow(header, relatedAdapter))
            mPresenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())
        }
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

                val action = BangumiDetailsFragmentDirections.actionBangumiDetailsFragmentToEpisodesSearchFragment(keyword)
                action.animeId = args.animeId
                action.episodeId = item.episodeId
                findNavController().navigate(action)
            }
            is HomeImageBean -> {
                val action = BangumiDetailsFragmentDirections.actionBangumiDetailsFragmentRelatedVideos(item.animeId)

                val cardView = holder.view as MainAreaCardView
                val options = makeSceneTransitionAnimation(requireActivity(), Pair(cardView.getImageView(), getString(R.string.transition_image)))
                val extras = ActivityNavigatorExtras(options)
                findNavController().navigate(action, extras)
            }
        }
    }

}