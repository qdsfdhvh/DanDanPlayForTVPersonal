package com.seiko.tv.ui.widget.presenter

import androidx.leanback.widget.Presenter
import androidx.leanback.widget.PresenterSelector
import com.seiko.common.imageloader.ImageLoader
import com.seiko.tv.data.db.model.BangumiEpisodeEntity
import com.seiko.tv.data.db.model.ResMagnetItemEntity
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.data.model.HomeSettingBean
import com.seiko.tv.data.model.api.SearchAnimeDetails
import javax.inject.Inject
import kotlin.reflect.KClass

class BangumiPresenterSelector @Inject constructor(
    private val imageLoader: ImageLoader
) : PresenterSelector() {

    private val presenterMap = HashMap<KClass<*>, Presenter>()

    override fun getPresenter(item: Any?): Presenter {
        if (item == null) {
            throw RuntimeException("The PresenterSelector not supports data items of null")
        }

        var presenter = presenterMap[item::class]
        if (presenter == null) {
            presenter = when (item) {
                is HomeImageBean -> HomeImageBeanPresenter(imageLoader)
                is HomeSettingBean -> HomeSettingBeanPresenter(imageLoader)
                is BangumiEpisodeEntity -> BangumiEpisodePresenter()
                is SearchAnimeDetails -> SearchBangumiPresenter(imageLoader)
                is ResMagnetItemEntity -> SearchMagnetPresenter()
                else -> throw RuntimeException(
                    String.format(
                        "The PresenterSelector not supports data items of type '%s'",
                        item.javaClass.name
                    )
                )
            }
            presenterMap[item::class] = presenter
        }
        return presenter
    }

}