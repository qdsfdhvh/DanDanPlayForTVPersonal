package com.seiko.domain.repo

import com.seiko.domain.model.ResMagnetItem
import com.seiko.domain.model.SearchAnimeDetails
import com.seiko.domain.utils.Result

interface SearchRepository {
    /**
     * 搜索对应的作品信息
     * PS: 1. 搜索结果中不包含剧集信息
     *     2. 关键词长度至少为2。
     *     3. 关键词中的空格将被认定为 AND 条件，其他字符将被作为原始字符去搜索。
     *     4. 可以通过中文、日文、罗马音、英文等条件对作品的别名进行搜索，繁体中文关键词将被统一为简体中文。
     * @param keyword 关键字
     * @param type 作品类型 ['', 'tvseries', 'tvspecial', 'ova', 'movie', 'musicvideo', 'web',
     *             'other', 'jpmovie', 'jpdrama', 'unknown']
     */
    suspend fun searchBangumiList(keyword: String, type: String): Result<List<SearchAnimeDetails>>

    /**
     * 搜索磁力连接
     * @param keyword 关键字
     * @param typeId 作品类型， 不需要输入-1
     * @param subGroupId 字幕组Id， 不需要输入-1
     */
    suspend fun searchMagnetList(keyword: String, typeId: Int, subGroupId: Int): Result<List<ResMagnetItem>>



}