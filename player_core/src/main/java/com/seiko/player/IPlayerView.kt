package com.seiko.player

interface IPlayerView {

    // 内部事件回调
    fun setOnInfoListener(listener: OnOutsideListener)

    // 跳转至上一次播放进度
    fun setSkipTip(position: Int)

    // 跳转
    fun seekTo(position: Long)

    // 设置标题
    fun setTitle(title: String)

    // 设置视频路径
    fun setVideoPath(videoPath: String)

    // 设置字幕路径
    fun setSubtitlePath(subtitlePath: String)

    // 开始播放
    fun start()

    // 暂停播放
    fun pause()

    // 停止播放
    fun stop()

    /******************************************/

    fun onResume()

    fun onPause()

    fun onDestroy()

    fun onBackPressed(): Boolean

    companion object {
        //正常播放时，隐藏所有
        const val HIDE_VIEW_ALL = 0
        //自动消失时，隐藏上下控制栏、截图、锁屏、亮度声音跳转
        const val HIDE_VIEW_AUTO = 1
        //点击锁屏时，隐藏除锁屏外所有
        const val HIDE_VIEW_LOCK_SCREEN = 2
        //手势结束时，隐藏、亮度声音跳转
        const val HIDE_VIEW_END_GESTURE = 3
        //点击屏幕、点击返回时，隐藏三个设置、发送弹幕、屏蔽弹幕
        const val HIDE_VIEW_EDIT = 4

        // 进度条最大值
        const val MAX_VIDEO_SEEK = 1000
        // 默认隐藏控制栏时间
        const val DEFAULT_HIDE_TIMEOUT = 5000
        // 更新进度消息
        const val MSG_UPDATE_SEEK = 10086
        // 延迟屏幕翻转消息
        const val MSG_ENABLE_ORIENTATION = 10087
        // 更新字幕消息
        const val MSG_UPDATE_SUBTITLE = 10088
        //设置字幕源
        const val MSG_SET_SUBTITLE_SOURCE = 10089
        // 无效变量
        const val INVALID_VALUE = -1L
    }
}