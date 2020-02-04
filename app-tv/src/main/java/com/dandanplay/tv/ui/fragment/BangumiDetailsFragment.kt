package com.dandanplay.tv.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.app.FixDetailsSupportFragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.dandanplay.tv.R
import com.dandanplay.tv.data.db.model.BangumiDetailsEntity
import com.dandanplay.tv.data.db.model.BangumiEpisodeEntity
import com.dandanplay.tv.data.db.model.BangumiIntroEntity
import com.dandanplay.tv.data.model.EpisodesListRow
import com.dandanplay.tv.ui.presenter.*
import com.dandanplay.tv.vm.BangumiDetailViewModel
import com.seiko.common.data.ResultData
import com.seiko.common.data.Status
import com.seiko.common.ui.dialog.setLoadFragment
import com.seiko.common.util.toast.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class BangumiDetailsFragment : FixDetailsSupportFragment()
    , CoroutineScope by MainScope()
    , OnItemViewClickedListener
    , OnActionClickedListener {

    private val args by navArgs<BangumiDetailsFragmentArgs>()
    private val viewModel by viewModel<BangumiDetailViewModel>()

    private var mPresenterSelector: ClassPresenterSelector? = null
    private var mAdapter: ArrayObjectAdapter? = null
    private var mActionAdapter: ArrayObjectAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        bindViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onItemViewClickedListener = null
        mPresenterSelector = null
        mAdapter = null
        mActionAdapter = null
        cancel()
    }

    /**
     * 生成相关UI
     */
    private fun setupUI() {
        mPresenterSelector = ClassPresenterSelector()
        mAdapter = ArrayObjectAdapter(mPresenterSelector)
        onItemViewClickedListener = this
        adapter = mAdapter
    }

    /**
     * 开始加载数据
     */
    private fun bindViewModel() {
        viewModel.mainState.observe(this::getLifecycle, this::updateUI)
        if (viewModel.mainState.value == null) {
            viewModel.getBangumiDetails(args.animeId)
        }
    }

    /**
     * 加载'动漫详情'数据
     */
    private fun updateUI(data: ResultData<Pair<BangumiDetailsEntity, Palette?>>) {
        when(data.responseType) {
            Status.LOADING -> {
                setLoadFragment(true)
            }
            Status.ERROR -> {
                setLoadFragment(false)
                toast(data.error.toString())
            }
            Status.SUCCESSFUL -> {
                setLoadFragment(false)
                updateDetails(data.data)
            }
        }
    }

    /**
     * 更新动漫详情
     */
    private fun updateDetails(pair: Pair<BangumiDetailsEntity, Palette?>?) {
        if (pair == null) return
        val details = pair.first
        val palette = pair.second
//        mAdapter.clear()
        setupDetailsOverviewRowPresenter(palette)
        setupDetailsOverviewRow(details)
        setupEpisodesRows(details.episodes)
        setupRelatedsRows(details.relateds)
        setupSimilarsRows(details.similars)
        prepareEntranceTransition()
    }

    /**
     * 添加简介布局选择器
     */
    private fun setupDetailsOverviewRowPresenter(palette: Palette?) {
        val logoPresenter = CustomDetailsOverviewLogoPresenter()
        val descriptionPresenter = CustomDetailsDescriptionPresenter()
        val descriptionRowPresenter = CustomFullWidthDetailsOverviewRowPresenter(descriptionPresenter, logoPresenter)
        descriptionRowPresenter.onActionClickedListener = this@BangumiDetailsFragment
        val swatch = palette?.darkMutedSwatch
        if (swatch != null) {
            descriptionPresenter.setColor(swatch.titleTextColor, swatch.bodyTextColor)
            descriptionRowPresenter.backgroundColor = swatch.rgb
            val hsv = FloatArray(3)
            val color = swatch.rgb
            Color.colorToHSV(color, hsv)
            hsv[2] *= 0.8f
            descriptionRowPresenter.actionsBackgroundColor = Color.HSVToColor(hsv)
            mAdapter!!.notifyItemRangeChanged(0, 1)
        }
        mPresenterSelector!!.addClassPresenter(DetailsOverviewRow::class.java, descriptionRowPresenter)
    }

    /**
     * 添加简介布局数据
     */
    private fun setupDetailsOverviewRow(details: BangumiDetailsEntity) {
        val detailsOverviewRow = DetailsOverviewRow(details)
        detailsOverviewRow.setImageBitmap(requireActivity(), null)
        detailsOverviewRow.isImageScaleUpAllowed = true
        mActionAdapter = ArrayObjectAdapter()
        mActionAdapter!!.add(
            Action(ID_RATING, "评分:${details.rating}", null,
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_rating))
        )
        mActionAdapter!!.add(
            if (details.isFavorited) {
                Action(ID_FAVOURITE, "已收藏", null,
                    ContextCompat.getDrawable(requireActivity(), R.drawable.ic_heart_full))
            } else {
                Action(ID_FAVOURITE, "未收藏", null,
                    ContextCompat.getDrawable(requireActivity(), R.drawable.ic_heart_empty))
            }
        )
        detailsOverviewRow.actionsAdapter = mActionAdapter
        mAdapter!!.add(detailsOverviewRow)
    }

    /**
     * 添加 分集
     */
    private fun setupEpisodesRows(episodes: List<BangumiEpisodeEntity>) {
        if (episodes.isNotEmpty()) {
            val episodesAdapter = ArrayObjectAdapter(BangumiEpisodePresenter())
            episodesAdapter.addAll(0, episodes)
            val header = HeaderItem(0, "分集")
            mAdapter!!.add(EpisodesListRow(header, episodesAdapter))
            mPresenterSelector!!.addClassPresenter(EpisodesListRow::class.java, EpisodesListRowPresenter(0))
        }
    }

    /**
     * 添加 其他系列
     */
    private fun setupRelatedsRows(relateds: List<BangumiIntroEntity>) {
        if (relateds.isNotEmpty()) {
            val relatedAdapter = ArrayObjectAdapter(BangumiRelatedPresenter())
            relatedAdapter.addAll(0, relateds)
            val header = HeaderItem(0, "其他系列")
            mAdapter!!.add(ListRow(header, relatedAdapter))
            mPresenterSelector!!.addClassPresenter(ListRow::class.java, ListRowPresenter())
        }
    }

    /**
     * 添加 相似作品
     */
    private fun setupSimilarsRows(similars: List<BangumiIntroEntity>) {
        if (similars.isNotEmpty()) {
            val relatedAdapter = ArrayObjectAdapter(BangumiRelatedPresenter())
            relatedAdapter.addAll(0, similars)
            val header = HeaderItem(0, "相似作品")
            mAdapter!!.add(ListRow(header, relatedAdapter))
            mPresenterSelector!!.addClassPresenter(ListRow::class.java, ListRowPresenter())
        }
    }

    /**
     * 点击：收藏
     */
    override fun onActionClicked(action: Action?) {
        when(action?.id) {
            ID_FAVOURITE -> {
                launch {
                    if (viewModel.setFavourite()) {
                        Timber.d("已收藏")
                        action.label1 = "已收藏"
                        action.icon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_heart_full)
                    } else {
                        Timber.d("未收藏")
                        action.label1 = "未收藏"
                        action.icon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_heart_empty)
                    }
                    val index = mActionAdapter!!.indexOf(action)
                    mActionAdapter!!.notifyItemRangeChanged(index, 1)
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
                val details = viewModel.animeDetails ?: return
                val keyword = viewModel.getSearchKey(item)

                val action = BangumiDetailsFragmentDirections.actionBangumiDetailsFragmentToEpisodesSearchFragment(keyword)
                action.animeId = details.animeId
                action.episodeId = item.episodeId
                findNavController().navigate(action)
            }
            is BangumiIntroEntity -> {
                findNavController().navigate(
                    BangumiDetailsFragmentDirections.actionBangumiDetailsFragmentRelatedVideos(
                        item.animeId
                    )
                )
            }
        }
    }

    companion object {
        private const val ID_RATING = 1L
        private const val ID_FAVOURITE = 2L
    }

}