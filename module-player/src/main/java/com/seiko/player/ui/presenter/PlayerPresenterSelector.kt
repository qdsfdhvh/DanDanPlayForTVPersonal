package com.seiko.player.ui.presenter

import androidx.leanback.widget.Presenter
import androidx.leanback.widget.PresenterSelector
import com.seiko.player.data.db.model.VideoMedia

class PlayerPresenterSelector : PresenterSelector() {

    override fun getPresenter(item: Any?): Presenter {
        return when(item) {
            is VideoMedia -> VideoMediaPresenter()
            else ->
                throw RuntimeException(String.format(
                    "The PresenterSelector not supports data items of type '%s'",
                    item?.javaClass?.name
                )
            )
        }
    }

}