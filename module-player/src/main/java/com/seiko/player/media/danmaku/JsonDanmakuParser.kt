package com.seiko.player.media.danmaku

import android.graphics.Color
import android.text.TextUtils
import com.seiko.player.data.model.DanmaDownloadBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import master.flame.danmaku.danmaku.model.*
import master.flame.danmaku.danmaku.model.IDanmakus.ST_BY_TIME
import master.flame.danmaku.danmaku.model.android.DanmakuFactory
import master.flame.danmaku.danmaku.model.android.Danmakus
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser
import master.flame.danmaku.danmaku.util.DanmakuUtils
import org.json.JSONArray
import org.json.JSONException
import org.koin.ext.isFloat
import java.lang.Float.parseFloat

class JsonDanmakuParser(private val danma: DanmaDownloadBean) : BaseDanmakuParser() {

    companion object {
        private const val SEP = ','
        private const val TRUE_STRING = "true"
    }

    private var mDispScaleX = 0f
    private var mDispScaleY = 0f

    // <d p="23.826000213623,1,25,16777215,1422201084,0,057075e9,757076900">我从未见过如此厚颜无耻之猴</d>
    // 0:时间(弹幕出现时间)
    // 1:类型(1从右至左滚动弹幕|6从左至右滚动弹幕|5顶端固定弹幕|4底端固定弹幕|7高级弹幕|8脚本弹幕)
    // 2:字号
    // 3:颜色
    // 4:时间戳 ?
    // 5:弹幕池id
    // 6:用户hash
    // 7:弹幕id
    override fun parse(): IDanmakus {
        return runBlocking(Dispatchers.Default) {
            val danmaku = Danmakus(ST_BY_TIME, false, mContext.baseComparator)
            var item: BaseDanmaku
            danma.comments.forEachIndexed { index, comment ->
                // {"cid":1580802879,"p":"187.10,1,16777215,[BiliBili]204e7d20","m":"握手言核"}
                // 187.10,1,25,16777215,[BiliBili]204e7d20,0,0,0
                val values = comment.p.split(SEP)
                if (values.isNotEmpty()) {
                    val time = ((values[0].toFloatOrNull() ?: 0f) * 1000) // 出现时间
                    val type: Int = values[1].toIntOrNull() ?: 1 // 弹幕类型
                    val textSize = 25f // 字体大小
                    val color = -0x1000000 or (values[2].toIntOrNull() ?: 0) // 颜色

                    item = mContext.mDanmakuFactory.createDanmaku(type, mContext) ?: return@forEachIndexed
                    item.time = time.toLong()
                    item.textSize = textSize * (mDispDensity - 0.6f)
                    item.textColor = color
                    item.textShadowColor = if (color <= Color.BLACK) Color.WHITE else Color.BLACK
                    item.index = index
                    parseDanmaText(item, comment.m)
                    if (item.duration != null) {
                        item.timer = timer
                        item.flags = mContext.mGlobalFlagValues
                        val lock: Any = danmaku.obtainSynchronizer()
                        synchronized(lock) { danmaku.addItem(item) }
                    }
                }
            }
            danmaku
        }
    }

    override fun setDisplayer(disp: IDisplayer?): BaseDanmakuParser? {
        super.setDisplayer(disp)
        mDispScaleX = mDispWidth / DanmakuFactory.BILI_PLAYER_WIDTH
        mDispScaleY = mDispHeight / DanmakuFactory.BILI_PLAYER_HEIGHT
        return this
    }

    private fun parseDanmaText(item: BaseDanmaku, m: String) {
        // initial specail danmaku data
        // initial specail danmaku data
        item.text = m
        val text: String = m.trim { it <= ' ' }
        if (item.type == BaseDanmaku.TYPE_SPECIAL && text.startsWith("[") && text.endsWith("]")) { //text = text.substring(1, text.length() - 1);
            var textArr: Array<String>? = null //text.split(",", -1);
            try {
                val jsonArray = JSONArray(text)
                textArr = Array(jsonArray.length()) { jsonArray.getString(it) }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            if (textArr == null || textArr.size < 5 || TextUtils.isEmpty(textArr[4])) {
                return
            }
            DanmakuUtils.fillText(item, textArr[4])
            var beginX: Float = textArr[0].toFloatOrNull() ?: 0f
            var beginY: Float = textArr[1].toFloatOrNull() ?: 0f
            var endX = beginX
            var endY = beginY
            val alphaArr = textArr[2].split("-").toTypedArray()
            val beginAlpha = AlphaValue.MAX * (alphaArr[0].toIntOrNull() ?: 0)
            var endAlpha = beginAlpha
            if (alphaArr.size > 1) {
                endAlpha = AlphaValue.MAX * (alphaArr[1].toIntOrNull() ?: 0)
            }
            val alphaDuration = ((textArr[3].toLongOrNull() ?: 0) * 1000)
            var translationDuration: Long = alphaDuration
            var translationStartDelay: Long = 0
            var rotateY = 0f
            var rotateZ = 0f
            if (textArr.size >= 7) {
                rotateZ = textArr[5].toFloatOrNull() ?: 0f
                rotateY = textArr[6].toFloatOrNull() ?: 0f
            }
            if (textArr.size >= 11) {
                endX = textArr[7].toFloatOrNull() ?: 0f
                endY = textArr[8].toFloatOrNull() ?: 0f
                if ("" != textArr[9]) {
                    translationDuration = textArr[9].toLongOrNull() ?: 0
                }
                if ("" != textArr[10]) {
                    translationStartDelay = textArr[10].toLongOrNull() ?: 0
                }
            }
            if (textArr[0].isFloat()) {
                beginX *= DanmakuFactory.BILI_PLAYER_WIDTH
            }
            if (textArr[1].isFloat()) {
                beginY *= DanmakuFactory.BILI_PLAYER_HEIGHT
            }
            if (textArr.size >= 8 && textArr[7].isFloat()) {
                endX *= DanmakuFactory.BILI_PLAYER_WIDTH
            }
            if (textArr.size >= 9 && textArr[8].isFloat()) {
                endY *= DanmakuFactory.BILI_PLAYER_HEIGHT
            }
            item.duration = Duration(alphaDuration)
            item.rotationZ = rotateZ
            item.rotationY = rotateY
            mContext.mDanmakuFactory.fillTranslationData(
                item,
                beginX,
                beginY,
                endX,
                endY,
                translationDuration,
                translationStartDelay,
                mDispScaleX,
                mDispScaleY
            )
            mContext.mDanmakuFactory.fillAlphaData(item, beginAlpha, endAlpha, alphaDuration)
            if (textArr.size >= 12) { // 是否有描边
                if (!TextUtils.isEmpty(textArr[11]) && TRUE_STRING.equals(textArr[11], true)) {
                    item.textShadowColor = Color.TRANSPARENT
                }
            }
//            if (textArr.size >= 13) {
//                // TODO 字体 textArr[12]
//            }
            if (textArr.size >= 14) { // Linear.easeIn or Quadratic.easeOut
                (item as SpecialDanmaku).isQuadraticEaseOut = "0" == textArr[13]
            }
            if (textArr.size >= 15) { // 路径数据
                if ("" != textArr[14]) {
                    val motionPathString = textArr[14].substring(1)
                    if (!TextUtils.isEmpty(motionPathString)) {
                        val pointStrArray =
                            motionPathString.split("L").toTypedArray()
                        if (pointStrArray.size > 0) {
                            val points =
                                Array(
                                    pointStrArray.size
                                ) { FloatArray(2) }
                            for (i in pointStrArray.indices) {
                                val pointArray =
                                    pointStrArray[i].split(",").toTypedArray()
                                if (pointArray.size >= 2) {
                                    points[i][0] = parseFloat(pointArray[0])
                                    points[i][1] = parseFloat(pointArray[1])
                                }
                            }
                            DanmakuFactory.fillLinePathData(
                                item, points,
                                mDispScaleX,
                                mDispScaleY
                            )
                        }
                    }
                }
            }
        }
    }

}