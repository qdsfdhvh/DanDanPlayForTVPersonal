package com.seiko.data.usecase

import com.seiko.data.utils.XLTaskHelperManager
import com.seiko.domain.entity.ThunderLocalUrl
import org.koin.core.KoinComponent
import org.koin.core.inject

class GetThunderLocalUrlUseCase : KoinComponent {

    private val taskHelperManager: XLTaskHelperManager by inject()

    operator fun invoke(checkedFilePosition: Int,
                        checkedFileSize: Long,
                        torrentFilePath: String): ThunderLocalUrl {
        return taskHelperManager.getLocalUrl(checkedFilePosition, checkedFileSize, torrentFilePath)
    }

}