package com.seiko.core.data.api.model

import com.google.gson.annotations.SerializedName
import com.seiko.core.model.api.ResMagnetItem

/**
 * HasMore : true
 * Resources : [
 * {"Title":"【傲娇零&自由字幕组】[刀剑神域III UnderWorld/Sword Art Online - Alicization][01][HEVC-10Bit-1080P AAC][外挂GB/BIG5][WEB-Rip][MP4+ass]",
 * "TypeId":2,
 * "TypeName":"动画/新番连载",
 * "SubgroupId":532,
 * "SubgroupName":"傲娇零字幕组",
 * "Magnet":"magnet:?xt=urn:btih:WEORDPJIJANN54BH2GNNJ6CSN7KB7S34",
 * "PageUrl":"https://share.dmhy.org/topics/view/501340_III_UnderWorld_Sword_Art_Online_-_Alicization_01_HEVC-10Bit-2160P_AAC_GB_BIG5_WEB-Rip_MP4_ass.html",
 * "FileSize":"818.7MB",
 * "PublishDate":"2018-10-12 12:44:00"
 *  }
 * ]
 */
class ResMagnetSearchResponse {
    @SerializedName("HasMore") var hasMore: Boolean = false
    @SerializedName("Resources") var resources: List<ResMagnetItem> = emptyList()
}