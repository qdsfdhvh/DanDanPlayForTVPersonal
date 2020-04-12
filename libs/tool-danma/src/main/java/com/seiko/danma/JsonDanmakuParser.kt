package com.seiko.danma

import android.graphics.Color
import android.text.TextUtils
import com.seiko.danma.model.Danma
import com.seiko.danma.model.SingleDanma
import com.seiko.danma.util.log
import master.flame.danmaku.danmaku.model.*
import master.flame.danmaku.danmaku.model.IDanmakus.ST_BY_TIME
import master.flame.danmaku.danmaku.model.android.DanmakuFactory
import master.flame.danmaku.danmaku.model.android.Danmakus
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser
import master.flame.danmaku.danmaku.util.DanmakuUtils
import org.json.JSONArray
import org.json.JSONException
import java.lang.Float.parseFloat
import kotlin.collections.ArrayList

class JsonDanmakuParser(private val danmaList: List<Danma>) : BaseDanmakuParser() {

    companion object {
        private const val SEP = ","
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
        val danmaku = Danmakus(ST_BY_TIME, false, mContext.baseComparator)
        parseDanmaList(danmaku, danmaList)
        return danmaku
    }

    override fun setDisplayer(disp: IDisplayer?): BaseDanmakuParser? {
        super.setDisplayer(disp)
        mDispScaleX = mDispWidth / DanmakuFactory.BILI_PLAYER_WIDTH
        mDispScaleY = mDispHeight / DanmakuFactory.BILI_PLAYER_HEIGHT
        return this
    }

    private fun parseDanmaList(danmaku: IDanmakus, danmaList: List<Danma>) {
        log("Parse danma begin...")
        val start = System.currentTimeMillis()

        val singleDanmaList = ArrayList<SingleDanma>(danmaku.size())
        val sepSize = SEP.length
        var p: String
        var currentOffset: Int
        var nextIndex: Int
        var singleDanma: SingleDanma
        var index: Int
        var text: String
        for (danma in danmaList) {
            p = danma.p()
            currentOffset = 0
            nextIndex = p.indexOf(SEP, currentOffset, true)
            if (nextIndex == -1) continue

            singleDanma = SingleDanma(danma.m())

            index = 0
            do {
                text = p.substring(currentOffset, nextIndex)
                when(index++) {
                    0 -> singleDanma.time = text.toFloatOrNull() ?: 0f
                    1 -> singleDanma.type = text.toIntOrNull() ?: 1
                    2 -> singleDanma.textColor = text.toIntOrNull() ?: 0
                }
                currentOffset = nextIndex + sepSize
                nextIndex = p.indexOf(SEP, currentOffset, true)
            } while (nextIndex != -1)
            singleDanmaList.add(singleDanma)
        }
        log("Parse danma map: 耗时：${System.currentTimeMillis() - start}")

        var item: BaseDanmaku
        var color: Int
        singleDanmaList.forEachIndexed { i, danma ->
            item = mContext.mDanmakuFactory.createDanmaku(danma.type, mContext) ?: return@forEachIndexed
            item.time = (danma.time * 1000).toLong()
            item.textSize = danma.textSize * (mDispDensity - 0.6f)
            color = -0x1000000 or danma.textColor
            item.textColor = color
            item.textShadowColor = if (color <= Color.BLACK) Color.WHITE else Color.BLACK
            item.index = i
            parseDanmaText(item, danma.m)
            if (item.duration != null) {
                item.timer = timer
                item.flags = mContext.mGlobalFlagValues
                danmaku.addItem(item)
            }
        }

        log("Parse danma finish: 耗时：${System.currentTimeMillis() - start}")
    }

    private fun parseDanmaText(item: BaseDanmaku, m: String) {
        item.text = m
        val text: String = m.trim { it <= ' ' }
        if (item.type == BaseDanmaku.TYPE_SPECIAL
            && text.startsWith("[")
            && text.endsWith("]")
        ) {
            var textArr: Array<String>? = null
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
//
//            }
            if (textArr.size >= 14) { // Linear.easeIn or Quadratic.easeOut
                (item as SpecialDanmaku).isQuadraticEaseOut = "0" == textArr[13]
            }
            if (textArr.size >= 15) { // 路径数据
                if ("" != textArr[14]) {
                    val motionPathString = textArr[14].substring(1)
                    if (!TextUtils.isEmpty(motionPathString)) {
                        val pointStrArray = motionPathString.split("L").toTypedArray()
                        if (pointStrArray.isNotEmpty()) {
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

    private fun String.isFloat() = this.toFloatOrNull() != null
}