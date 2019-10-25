package com.seiko.player

interface OnOutsideListener {

    fun onAction(action: Int, extra: Long)

    companion object {
        //打开字幕选择
        const val INTENT_OPEN_DANMU = 1000
        //打开字幕选择
        const val INTENT_OPEN_SUBTITLE = 1001
        //请求网络字幕
        const val INTENT_QUERY_SUBTITLE = 1002
        //选择网络字幕
        const val INTENT_SELECT_SUBTITLE = 1003
        //自动加载网络字幕
        const val INTENT_AUTO_SUBTITLE = 1004
        //保存进度
        const val INTENT_SAVE_CURRENT = 1005
        //重置全屏
        const val INTENT_RESET_FULL_SCREEN = 1006
        //播放失败
        const val INTENT_PLAY_FAILED = 1007
        //播放结束
        const val INTENT_PLAY_END = 1008
    }
}