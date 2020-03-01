package com.seiko.player.ui.video

//import android.os.Handler
//import android.os.Looper
//import android.os.Message
//import android.view.ViewConfiguration
//import java.lang.ref.WeakReference
//
//
//class VideoPlayerHandler(activity: VideoPlayerActivity) : Handler(Looper.getMainLooper()) {
//
//    companion object {
//        private const val MSG_UPDATE_SEEK = 100
//        private const val SET_CONTROL_SHOW = 10
//        private const val SET_OVERLAY_SHOW = 11
//        private const val SET_OPTIONS_SHOW = 12
//        private const val SET_VIDEO_SEEK = 14
////        private const val SET_VIDEO_SEEK_DELTA = 15
//        private const val SET_VIDEO_PLAY = 16
//    }
//
//    private val reference = WeakReference(activity)
//
//    override fun handleMessage(msg: Message) {
//        val activity = reference.get() ?: return
//        when(msg.what) {
//            MSG_UPDATE_SEEK -> {
//                val pos = activity.updateProgress()
//                if (activity.isPlaying()) {
//                    val obtainMsg = obtainMessage(MSG_UPDATE_SEEK)
//                    sendMessageDelayed(obtainMsg, 1000 - (pos % 1000))
//                }
//            }
//            SET_CONTROL_SHOW -> {
//                activity.setControlShow()
//            }
//            SET_OVERLAY_SHOW -> {
//                activity.setOverlayShow(msg.obj as? Boolean)
//            }
//            SET_OPTIONS_SHOW -> {
//                activity.setOptionsShow(msg.obj as? Boolean)
//            }
//            SET_VIDEO_SEEK -> {
//                activity.seekTo(msg.obj as? Long)
//            }
////            SET_VIDEO_SEEK_DELTA -> {
////                activity.seekDelta(msg.obj as? Long)
////            }
//        }
//    }
//
//    /**
//     * 开始更新进度条
//     */
//    fun startUpdateProgress(delay: Long = 0) {
//        removeMessages(MSG_UPDATE_SEEK)
//        sendEmptyMessageDelayed(MSG_UPDATE_SEEK, delay)
//    }
//
//    /**
//     * 停止更新进度条
//     */
//    fun stopUpdateProgress() {
//        removeMessages(MSG_UPDATE_SEEK)
//    }
//
//    /**
//     * 跳转
//     * @param position 具体位置
//     * @param delay 延时ms
//     */
//    fun seekTo(position: Long, delay: Long = 400) {
//        removeMessages(SET_VIDEO_SEEK)
//        val msg = obtainMessage(SET_VIDEO_SEEK, position)
//        sendMessageDelayed(msg, delay)
//    }
//
////    /**
////     * 快进/快退
////     * @param delta 快进or快退时间
////     */
////    fun seekDelta(delta: Long) {
////        val msg = obtainMessage(SET_VIDEO_SEEK_DELTA, delta)
////        sendMessageDelayed(msg, ViewConfiguration.getDoubleTapTimeout().toLong())
////    }
//
//    /**
//     * 点击屏幕，切换控制相关界面的显示/隐藏
//     * @param delay 延时ms
//     */
//    fun controlShow(delay: Long = ViewConfiguration.getDoubleTapTimeout().toLong()) {
//        sendEmptyMessageDelayed(SET_CONTROL_SHOW, delay)
//    }
//
//    /**
//     * 显示/隐藏 底部控制界面
//     * @param show 是否显示，null时切换当前状态
//     * @param delay 延时ms
//     */
//    fun overlayShow(
//        show: Boolean? = null,
//        delay: Long = ViewConfiguration.getDoubleTapTimeout().toLong()
//    ) {
//        removeMessages(SET_OVERLAY_SHOW)
//        val msg = obtainMessage(SET_OVERLAY_SHOW, show)
//        sendMessageDelayed(msg, delay)
//    }
//
//    /**
//     * 显示/隐藏 右侧配置界面
//     * @param show 是否显示，null时切换当前状态
//     * @param delay 延时ms
//     */
//    fun optionsShow(
//        show: Boolean? = null,
//        delay: Long = ViewConfiguration.getDoubleTapTimeout().toLong()
//    ) {
//        removeMessages(SET_OPTIONS_SHOW)
//        val msg = obtainMessage(SET_OPTIONS_SHOW, show)
//        sendMessageDelayed(msg, delay)
//    }
//
//}