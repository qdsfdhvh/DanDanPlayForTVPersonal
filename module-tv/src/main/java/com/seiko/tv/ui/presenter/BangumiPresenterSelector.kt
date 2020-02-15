package com.seiko.tv.ui.presenter

import androidx.leanback.widget.Presenter
import androidx.leanback.widget.PresenterSelector
import com.seiko.tv.data.db.model.BangumiEpisodeEntity
import com.seiko.tv.data.db.model.ResMagnetItemEntity
import com.seiko.tv.data.model.HomeImageBean
import com.seiko.tv.data.model.HomeSettingBean
import com.seiko.tv.data.model.api.SearchAnimeDetails

class BangumiPresenterSelector : PresenterSelector() {

    override fun getPresenter(item: Any?): Presenter {
        return when(item) {
            is HomeImageBean -> HomeImageBeanPresenter()
            is HomeSettingBean -> HomeSettingBeanPresenter()
            is BangumiEpisodeEntity -> BangumiEpisodePresenter()
            is SearchAnimeDetails -> SearchBangumiPresenter()
            is ResMagnetItemEntity -> SearchMagnetPresenter()
            else ->
                throw RuntimeException(String.format(
                    "The PresenterSelector not supports data items of type '%s'",
                    item?.javaClass?.name
                )
            )
        }
    }

}