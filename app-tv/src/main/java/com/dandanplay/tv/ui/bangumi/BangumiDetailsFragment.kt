package com.dandanplay.tv.ui.bangumi

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.dandanplay.tv.R
import com.dandanplay.tv.ui.dialog.setLoadFragment
import com.dandanplay.tv.ui.presenter.*
import com.dandanplay.tv.models.EpisodesListRow
import com.dandanplay.tv.vm.BangumiDetailViewModel
import com.facebook.common.executors.CallerThreadExecutor
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.seiko.common.ResultData
import com.seiko.common.Status
import com.seiko.domain.model.api.BangumiDetails
import com.seiko.domain.model.api.BangumiEpisode
import com.seiko.domain.model.api.BangumiIntro
import org.koin.android.viewmodel.ext.android.viewModel

class BangumiDetailsFragment : DetailsSupportFragment(), OnItemViewClickedListener,
    OnActionClickedListener {

    private val args by navArgs<BangumiDetailsFragmentArgs>()

    private val viewModel by viewModel<BangumiDetailViewModel>()

    private lateinit var mPresenterSelector: ClassPresenterSelector
    private lateinit var mAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    /**
     * 生成相关UI
     */
    private fun setupUI() {
        if (adapter != null) return
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
    private fun updateUI(data: ResultData<BangumiDetails>) {
        when(data.responseType) {
            Status.LOADING -> {
                setLoadFragment(true)
            }
            Status.ERROR -> {
                setLoadFragment(false)
                ToastUtils.showShort(data.error.toString())
            }
            Status.SUCCESSFUL -> {
                updateDetails(data.data)
                setLoadFragment(false)
            }
        }
    }

    /**
     * 更新动漫详情
     */
    private fun updateDetails(details: BangumiDetails?) {
        if (details == null) return
//        title = details.animeTitle
        viewModel.palette.observe(this::getLifecycle) { palette ->
            mAdapter.clear()
            setupDetailsOverviewRowPresenter(palette)
            setupDetailsOverviewRow(details)
            setupEpisodesRows(details.episodes)
            setupRelatedsRows(details.relateds)
            setupSimilarsRows(details.similars)
            prepareEntranceTransition()
        }
        getImagePaletteSync(details.imageUrl)
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
            mAdapter.notifyItemRangeChanged(0, 1)
        }
        mPresenterSelector.addClassPresenter(DetailsOverviewRow::class.java, descriptionRowPresenter)
    }

    /**
     * 添加简介布局数据
     */
    private fun setupDetailsOverviewRow(details: BangumiDetails) {
        val detailsOverviewRow = DetailsOverviewRow(details)
        detailsOverviewRow.setImageBitmap(activity!!, null)
        detailsOverviewRow.isImageScaleUpAllowed = true
        val actionAdapter = SparseArrayObjectAdapter()
        actionAdapter.set(1, Action(ID_RATING, "评分:${details.rating}", null,
            ContextCompat.getDrawable(activity!!, R.drawable.ic_rating))
        )
        if (details.isFavorited) {
            actionAdapter.set(2, Action(ID_FAVOURITE, "已收藏", null,
                ContextCompat.getDrawable(activity!!, R.drawable.ic_heart_full))
            )
        } else {
            actionAdapter.set(2, Action(ID_FAVOURITE, "未收藏", null,
                ContextCompat.getDrawable(activity!!, R.drawable.ic_heart_empty))
            )
        }
        detailsOverviewRow.actionsAdapter = actionAdapter
        mAdapter.add(detailsOverviewRow)
    }

    /**
     * 添加 分集
     */
    private fun setupEpisodesRows(episodes: List<BangumiEpisode>) {
        if (episodes.isNotEmpty()) {
            val episodesAdapter = ArrayObjectAdapter(BangumiEpisodePresenter())
            episodesAdapter.addAll(0, episodes)
            val header = HeaderItem(0, "分集")
            mAdapter.add(EpisodesListRow(header, episodesAdapter))
            mPresenterSelector.addClassPresenter(EpisodesListRow::class.java, EpisodesListRowPresenter(0))
        }
    }

    /**
     * 添加 其他系列
     */
    private fun setupRelatedsRows(relateds: List<BangumiIntro>) {
        if (relateds.isNotEmpty()) {
            val relatedAdapter = ArrayObjectAdapter(BangumiRelatedPresenter())
            relatedAdapter.addAll(0, relateds)
            val header = HeaderItem(0, "其他系列")
            mAdapter.add(ListRow(header, relatedAdapter))
            mPresenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())
        }
    }

    /**
     * 添加 相似作品
     */
    private fun setupSimilarsRows(similars: List<BangumiIntro>) {
        if (similars.isNotEmpty()) {
            val relatedAdapter = ArrayObjectAdapter(BangumiRelatedPresenter())
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
                ToastUtils.showShort("点击 收藏")
            }
        }
    }

    /**
     * 点击：集数、相关工作
     */
    override fun onItemClicked(holder: Presenter.ViewHolder, item: Any?,
                               rowHolder: RowPresenter.ViewHolder?, row: Row?) {
        when(item) {
            is BangumiEpisode -> {
                val keyword = viewModel.getSearchKey(item)
                findNavController().navigate(
                    BangumiDetailsFragmentDirections.actionBangumiDetailsFragmentToEpisodesSearchFragment(
                        keyword, viewModel.animeTitle)
                )
            }
            is BangumiIntro -> {
                findNavController().navigate(
                    BangumiDetailsFragmentDirections.actionBangumiDetailsFragmentRelatedVideos(item.animeId)
                )
            }
        }
    }

    /**
     * 使用Fresco配合Palette，对网络图片取色
     */
    private fun getImagePaletteSync(imageUrl: String) {
        if (viewModel.equalImageUrl(imageUrl)) return
        LogUtils.d("加载图片：$imageUrl")

        val uri = Uri.parse(imageUrl)
        val imageRequest = ImageRequestBuilder.newBuilderWithSource(uri)
//            .setProgressiveRenderingEnabled(true)
            .build()
        val imagePipeline = Fresco.getImagePipeline()
        val dataSource = imagePipeline.fetchDecodedImage(imageRequest, activity!!)
        val dataSubscriber = object : BaseBitmapDataSubscriber() {
            override fun onNewResultImpl(bitmap: Bitmap?) {
                if (bitmap == null) {
                    return
                }

                Palette.Builder(bitmap).generate { palette ->
                    viewModel.palette.value = palette
                }
            }

            override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>>?) {

            }
        }
        dataSource.subscribe(dataSubscriber, CallerThreadExecutor.getInstance())
    }

    companion object {
        private const val ID_RATING = 1L
        private const val ID_FAVOURITE = 2L
    }

}