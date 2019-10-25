package com.dandanplay.tv.ui.bangumi

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import androidx.navigation.fragment.findNavController
import androidx.palette.graphics.Palette
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.dandanplay.tv.R
import com.dandanplay.tv.ui.dialog.setLoadFragment
import com.dandanplay.tv.ui.presenter.*
import com.dandanplay.tv.utils.ChaptersListRow
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
import com.seiko.domain.entities.BangumiDetails
import com.seiko.domain.entities.BangumiIntro
import com.seiko.domain.entities.BangumiTag
import org.koin.android.viewmodel.ext.android.viewModel

class BangumiDetailsFragment : DetailsSupportFragment(), OnItemViewClickedListener,
    OnActionClickedListener {

    private val viewModel by viewModel<BangumiDetailViewModel>()

//    private lateinit var mDetailsBackground: DetailsSupportFragmentBackgroundController
    private lateinit var mPresenterSelector: ClassPresenterSelector
    private lateinit var mRowsAdapter: ArrayObjectAdapter

    private var mBangumiIntro: BangumiIntro? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val safeArgs = BangumiDetailsFragmentArgs.fromBundle(it)
            mBangumiIntro = safeArgs.intro
        }
        mBangumiIntro ?: findNavController().popBackStack()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.mainState.observe(this::getLifecycle, this::updateUI)

//        mDetailsBackground = DetailsSupportFragmentBackgroundController(this)
        mPresenterSelector = ClassPresenterSelector()
        onItemViewClickedListener = this

        if (viewModel.mainState.value == null) {
            viewModel.getBangumiDetails(mBangumiIntro!!.animeId)
        }
    }

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
                setLoadFragment(false)
                updateDetails(data.data ?: return)
            }
        }
    }

    private fun updateDetails(details: BangumiDetails) {
        getImagePaletteSync(details.imageUrl) { bitmap, palette ->
            updateBackground(bitmap)

            val selector = ClassPresenterSelector()

            val swatch = palette?.darkMutedSwatch
            val descriptionPresenter = if (swatch == null) {
                DetailsDescriptionPresenter()
            } else {
                DetailsDescriptionPresenter(swatch.titleTextColor, swatch.bodyTextColor)
            }

            val rowPresenter = CustomFullWidthDetailsOverviewRowPresenter(descriptionPresenter)
            if (swatch != null) {
                rowPresenter.backgroundColor = swatch.rgb
                val hsv = FloatArray(3)
                val color = swatch.rgb
                Color.colorToHSV(color, hsv)
                hsv[2] *= 0.8f
                rowPresenter.actionsBackgroundColor = Color.HSVToColor(hsv)
            }
            rowPresenter.onActionClickedListener = this@BangumiDetailsFragment

            selector.addClassPresenter(DetailsOverviewRow::class.java, rowPresenter)
            selector.addClassPresenter(ChaptersListRow::class.java, ChaptersListPresenter(0))
            selector.addClassPresenter(ListRow::class.java, ListRowPresenter())
            mRowsAdapter = ArrayObjectAdapter(selector)

            val detailsOverview = DetailsOverviewRow(details)
            detailsOverview.setImageBitmap(activity!!, bitmap)
            detailsOverview.isImageScaleUpAllowed = true
            val actionAdapter = SparseArrayObjectAdapter()
            if (details.isFavorited) {
                actionAdapter.set(1, Action(ID_FAVOURITE, "已收藏", null,
                        ContextCompat.getDrawable(activity!!, R.drawable.ic_heart_full))
                )
            } else {
                actionAdapter.set(1, Action(ID_FAVOURITE, "未收藏", null,
                        ContextCompat.getDrawable(activity!!, R.drawable.ic_heart_empty))
                )
            }
            detailsOverview.actionsAdapter = actionAdapter
            mRowsAdapter.add(detailsOverview)

            // 集数
            if (details.episodes.isNotEmpty()) {
                val episodesAdapter = ArrayObjectAdapter(BangumiEpisodePresenter())
                episodesAdapter.addAll(0, details.episodes)
                val header = HeaderItem(0, "分集")
                mRowsAdapter.add(ChaptersListRow(header, episodesAdapter))
            }

            // 相关作品
            if (details.relateds.isNotEmpty()) {
                val relatedAdapter = ArrayObjectAdapter(BangumiRelatedPresenter())
                relatedAdapter.addAll(0, details.relateds)
                val header = HeaderItem(0, "相关作品")
                mRowsAdapter.add(ListRow(header, relatedAdapter))
            }

            adapter = mRowsAdapter
        }

    }

    private fun updateBackground(bitmap: Bitmap?) {
        if (bitmap == null) return
//        mDetailsBackground.enableParallax()
//        mDetailsBackground.coverBitmap = bitmap
    }

    override fun onActionClicked(action: Action?) {
        when(action?.id) {
            ID_FAVOURITE -> {
                ToastUtils.showShort("点击 收藏")
            }
        }
    }

    override fun onItemClicked(holder: Presenter.ViewHolder?,
                               item: Any?,
                               rowHolder: RowPresenter.ViewHolder?,
                               row: Row?) {
        when(item) {
            is BangumiIntro -> {
//                start(newInstance(item.animeId))
                findNavController().navigate(
                    BangumiDetailsFragmentDirections.actionBangumiDetailsFragmentRelatedVideos(item)
                )
            }
            is BangumiTag -> {
                ToastUtils.showShort(item.name)
            }
        }
    }

//    /**
//     * 传给{@link [MainActivity.onStartFragment]}需要启动的Fragment
//     */
//    private fun start(fragment: ISupportFragment) {
//        EventBusActivityScope.getDefault(mActivity).post(PostEvent.MainStart(fragment))
//    }

    /**
     * 使用Fresco配合Palette，对网络图片取色
     */
    private fun getImagePaletteSync(imageUrl: String, callback: (Bitmap?, Palette?) -> Unit) {
        val uri = Uri.parse(imageUrl)
        val imageRequest = ImageRequestBuilder.newBuilderWithSource(uri)
            .setProgressiveRenderingEnabled(true)
            .build()
        val imagePipeline = Fresco.getImagePipeline()
        val dataSource = imagePipeline.fetchDecodedImage(imageRequest, activity!!)
        val dataSubscriber = object : BaseBitmapDataSubscriber() {
            override fun onNewResultImpl(bitmap: Bitmap?) {
                if (bitmap == null) {
                    callback.invoke(null, null)
                    return
                }

                Palette.Builder(bitmap).generate { palette ->
                    callback.invoke(bitmap, palette)
                }
            }

            override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>>?) {
                callback.invoke(null, null)
            }
        }
        dataSource.subscribe(dataSubscriber, CallerThreadExecutor.getInstance())
    }

    companion object {

//        private const val ARGS_ANIME_ID = "ARGS_ANIME_ID"
//        private const val ARGS_IMAGE_URL = "ARGS_IMAGE_URL"

        private const val ID_FAVOURITE = 1L

//        fun newInstance(animeId: Int, imageUrl: String? = null): BangumiDetailsFragment {
//            val bundle = Bundle()
//            bundle.putInt(ARGS_ANIME_ID, animeId)
//            if (imageUrl != null) {
//                bundle.putString(ARGS_IMAGE_URL, imageUrl)
//            }
//            val fragment = BangumiDetailsFragment()
//            fragment.arguments = bundle
//            return fragment
//        }
    }

}