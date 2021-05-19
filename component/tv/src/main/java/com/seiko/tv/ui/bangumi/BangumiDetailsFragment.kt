package com.seiko.tv.ui.bangumi

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import com.github.fragivity.navigator
import com.github.fragivity.push
import com.seiko.common.imageloader.ImageLoader
import com.seiko.common.ui.adapter.AsyncObjectAdapter
import com.seiko.common.util.extensions.getDrawable
import com.seiko.common.util.extensions.hasFragment
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.tv.R
import com.seiko.tv.data.db.model.BangumiEpisodeEntity
import com.seiko.tv.data.model.BangumiDetailBean
import com.seiko.tv.data.model.EpisodesListRow
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.data.model.RelatesListRow
import com.seiko.tv.ui.search.SearchMagnetFragment
import com.seiko.tv.ui.widget.dialog.DialogInputFragment
import com.seiko.tv.ui.widget.presenter.BangumiPresenterSelector
import com.seiko.tv.ui.widget.presenter.CustomFullWidthDetailsOverviewRowPresenter
import com.seiko.tv.ui.widget.presenter.DetailsDescriptionPresenter
import com.seiko.tv.ui.widget.presenter.DetailsOverviewLogoPresenter
import com.seiko.tv.util.diff.HomeImageBeanDiffCallback
import com.seiko.tv.vm.BangumiDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class BangumiDetailsFragment : DetailsSupportFragment(), OnItemViewClickedListener,
    OnActionClickedListener {

    companion object {
        const val ARGS_ANIME_ID = "ARGS_ANIME_ID"
        const val ARGS_ANIME_IMAGE_URL = "ARGS_ANIME_IMAGE_URL"

        private const val ID_RATING = 1L
        private const val ID_FAVOURITE = 2L
        private const val ID_KEYBOARD = 3L

        fun newInstance(animeId: Long, imageUrl: String): BangumiDetailsFragment {
            return BangumiDetailsFragment().apply {
                arguments = bundleOf(
                    ARGS_ANIME_ID to animeId,
                    ARGS_ANIME_IMAGE_URL to imageUrl
                )
            }
        }
    }

    private val animeId: Long by lazyAndroid { requireArguments().getLong(ARGS_ANIME_ID) }
    private val viewModel: BangumiDetailViewModel by viewModels()

    @Inject
    lateinit var presenterSelector: BangumiPresenterSelector

    @Inject
    lateinit var imageLoader: ImageLoader

    private lateinit var mPresenterSelector: ClassPresenterSelector
    private lateinit var mAdapter: ArrayObjectAdapter
    private lateinit var mActionAdapter: ArrayObjectAdapter
    private lateinit var mDescriptionRowPresenter: CustomFullWidthDetailsOverviewRowPresenter

    private lateinit var detailsOverviewRow: DetailsOverviewRow
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
        prepareEntranceTransition()
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
        val logoPresenter = DetailsOverviewLogoPresenter()
        val descriptionPresenter = DetailsDescriptionPresenter()
        mDescriptionRowPresenter = CustomFullWidthDetailsOverviewRowPresenter(
            descriptionPresenter, logoPresenter
        )
        mDescriptionRowPresenter.setViewHolderState(mDetailsOverviewPrevState)
        mDescriptionRowPresenter.onActionClickedListener = this@BangumiDetailsFragment

        val imageUrl = requireArguments().getString(ARGS_ANIME_IMAGE_URL, "")
        detailsOverviewRow = DetailsOverviewRow(BangumiDetailBean.empty(imageUrl = imageUrl))
        detailsOverviewRow.isImageScaleUpAllowed = true

        mPresenterSelector = ClassPresenterSelector()
        mPresenterSelector.addClassPresenter(
            DetailsOverviewRow::class.java,
            mDescriptionRowPresenter
        )
        mPresenterSelector.addClassPresenter(EpisodesListRow::class.java, ListRowPresenter())
        mPresenterSelector.addClassPresenter(RelatesListRow::class.java, ListRowPresenter())
        mPresenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())

        mAdapter = ArrayObjectAdapter(mPresenterSelector)
        mAdapter.add(detailsOverviewRow)

        episodesAdapter = ArrayObjectAdapter(presenterSelector)
        mAdapter.add(EpisodesListRow(HeaderItem(0, "分集"), episodesAdapter))

        relatedAdapter = AsyncObjectAdapter(presenterSelector, homeImageBeanDiffCallback)
        relatedListRow = RelatesListRow(HeaderItem(0, "其他系列"), relatedAdapter)
        similarAdapter = AsyncObjectAdapter(presenterSelector, homeImageBeanDiffCallback)
        similarListRow = ListRow(HeaderItem(0, "相似作品"), similarAdapter)
        adapter = mAdapter
    }


    /**
     * 开始加载数据
     */
    private fun bindViewModel() {
        viewModel.details.observe(viewLifecycleOwner) { details ->
            updateDetailsOverviewRow(details)
            startEntranceTransition()
        }
        viewModel.episodesList.observe(viewLifecycleOwner) { episodes ->
            episodesAdapter.setItems(episodes, null)
        }
        viewModel.relatedsList.observe(viewLifecycleOwner) { relateds ->
            mAdapter.showHideListRow(relatedListRow, relateds)
            relatedAdapter.submitList(relateds)
        }
        viewModel.similarsList.observe(viewLifecycleOwner) { similars ->
            mAdapter.showHideListRow(similarListRow, similars)
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

        detailsOverviewRow.item = details
        lifecycleScope.launch {
            val drawable = withContext(Dispatchers.IO) {
                imageLoader.getDrawable(details.imageUrl)
            }
            detailsOverviewRow.imageDrawable = drawable
        }

        mActionAdapter = ArrayObjectAdapter()
        mActionAdapter.add(
            Action(ID_RATING, "评分：${details.rating}", null, getDrawable(R.drawable.ic_rating_24dp))
        )
        mActionAdapter.add(
            if (details.isFavorited) {
                Action(ID_FAVOURITE, "已收藏", null, getDrawable(R.drawable.ic_heart_full_24dp))
            } else {
                Action(ID_FAVOURITE, "未收藏", null, getDrawable(R.drawable.ic_heart_empty_24dp))
            }
        )
        mActionAdapter.add(
            Action(
                ID_KEYBOARD,
                "搜索关键字：",
                details.keyboard,
                getDrawable(R.drawable.ic_keyboard_24dp)
            )
        )
        detailsOverviewRow.actionsAdapter = mActionAdapter
    }

    /**
     * 点击：收藏
     */
    override fun onActionClicked(action: Action?) {
        when (action?.id) {
            ID_FAVOURITE -> {
                lifecycleScope.launch {
                    if (viewModel.setFavourite()) {
                        Timber.d("已收藏")
                        action.label1 = "已收藏"
                        action.icon = getDrawable(R.drawable.ic_heart_full_24dp)
                    } else {
                        Timber.d("未收藏")
                        action.label1 = "未收藏"
                        action.icon = getDrawable(R.drawable.ic_heart_empty_24dp)
                    }
                    mActionAdapter.notifyItemRangeChanged(action)
                }
            }
            ID_KEYBOARD -> {
                if (!hasFragment(DialogInputFragment.TAG)) {
                    DialogInputFragment.Builder()
                        .setConfirmText("确认")
                        .setCancelText("取消")
                        .setValue(action.label2.toString())
                        .setConfirmClickListener { keyboard ->
                            lifecycleScope.launch {
                                viewModel.saveKeyboard(keyboard)?.let { realKeyboard ->
                                    action.label2 = realKeyboard
                                    mActionAdapter.notifyItemRangeChanged(action)
                                }
                            }
                        }
                        .build()
                        .show(childFragmentManager)
                }
            }
        }
    }

    /**
     * 点击：集数、相关工作
     */
    override fun onItemClicked(
        holder: Presenter.ViewHolder, item: Any?,
        rowHolder: RowPresenter.ViewHolder?, row: Row?
    ) {
        when (item) {
            is BangumiEpisodeEntity -> {
                val keyword = viewModel.getSearchKey(item)
                navigator.push {
                    SearchMagnetFragment.newInstance(keyword)
                }
            }
            is HomeImageBean -> {
                navigator.push {
                    newInstance(item.animeId, item.imageUrl)
                }
            }
        }
    }

}

private fun <T> ArrayObjectAdapter.showHideListRow(listRow: ListRow, list: Collection<T>) {
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

private fun ArrayObjectAdapter.notifyItemRangeChanged(action: Action) {
    val index = indexOf(action)
    if (index >= 0) {
        notifyItemRangeChanged(index, 1)
    }
}