package com.seiko.danma

import master.flame.danmaku.danmaku.model.DanmakuTimer

/**
 * @see github [https://github.com/luern0313/DanmakuFlameMaster/blob/master/DanmakuFlameMaster/src/main/java/master/flame/danmaku/danmaku/model/DanmakuTimer.java]
 */
class SpeedDanmakuTimer @JvmOverloads constructor(curr: Long = 0) : DanmakuTimer(curr) {

    private var lastInterval: Long = 0

    private var videoSpeed = 1.0f
    private var lastTimeStamp = 0L
    private var lastCurr: Long = 0
    private var firstCurr: Long = 0

    override fun update(curr: Long): Long {
        if (lastTimeStamp == 0L) {
            lastTimeStamp = System.currentTimeMillis()
            firstCurr = curr
        }
        val t = System.currentTimeMillis()
        lastInterval = t - lastTimeStamp

        if (lastInterval - curr + lastCurr > 2000 || lastInterval - curr + lastCurr < -2000) currMillisecond =
            curr - firstCurr else currMillisecond += lastInterval * videoSpeed.toLong()

        lastCurr = curr
        lastTimeStamp = t
        return lastInterval
    }

    override fun add(mills: Long): Long {
       return update(currMillisecond + mills)
    }

    override fun lastInterval(): Long {
        return lastInterval
    }

    fun setSpeed(speed: Float) {
        videoSpeed = speed
    }

    fun getSpeed(): Float {
        return videoSpeed
    }

}