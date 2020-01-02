package com.dandanplay.tv.ui.player

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.dandanplay.tv.R
import com.dandanplay.tv.ui.dialog.SelectDialogFragment
import com.seiko.data.constants.DEFAULT_CACHE_FOLDER_PATH
import com.xunlei.downloadlib.XLTaskHelper

class PlayerManagerActivity: FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_manager)
        initView()
        initIntent()
    }

    private fun initView() {
        onBackPressedDispatcher.addCallback(this) { launchExitDialog() }
    }

    private fun initIntent() {
        val openIntent = intent ?: return
        val videoTitle = openIntent.getStringExtra(ARGS_VIDEO_TITLE) ?: ""
        val videoPath = openIntent.getStringExtra(ARGS_VIDEO_PATH) ?: ""
        if (videoPath.isEmpty()) {
            ToastUtils.showShort("播放连接无效。")
            return
        }

        if (supportFragmentManager.findFragmentByTag(ExoPlayerFragment.TAG) == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container,
                    ExoPlayerFragment.newInstance(videoTitle, videoPath),
                    ExoPlayerFragment.TAG)
                .commit()
        }
    }

    private fun launchExitDialog() {
        if (supportFragmentManager.findFragmentByTag(SelectDialogFragment.TAG) == null) {
            SelectDialogFragment.Builder()
                .setTitle("你真的确认退出播放吗？")
                .setConfirmText("确认")
                .setCancelText("取消")
                .setConfirmClickListener {
                    ActivityUtils.finishActivity(this, true)
                }
                .build()
                .show(supportFragmentManager)
        }
    }

    override fun onStop() {
        super.onStop()
        val thunderTaskId = intent?.getLongExtra(ARGS_THUNDER_TASK_ID, -1L) ?: -1L
        if (thunderTaskId != -1L) {
            LogUtils.d("停止thunderTaskId=$thunderTaskId。")
//            XLTaskHelper.stopTask(thunderTaskId)
//            FileUtils.deleteAllInDir(DEFAULT_CACHE_FOLDER_PATH)
            XLTaskHelper.deleteTask(thunderTaskId,
                DEFAULT_CACHE_FOLDER_PATH
            )
        }
        finish()
    }

    companion object {
        private const val ARGS_VIDEO_TITLE = "ARGS_VIDEO_TITLE"
        private const val ARGS_VIDEO_PATH = "ARGS_VIDEO_PATH"
        private const val ARGS_THUNDER_TASK_ID = "ARGS_THUNDER_TASK_ID"

        fun launchPlayerOnline(context: Context,
                               videoTitle: String,
                               videoPath: String,
                               danmuPath: String = "",
                               thunderTaskId: Long = -1L) {
            val intent = Intent(context, PlayerManagerActivity::class.java)
            intent.putExtra(ARGS_VIDEO_TITLE, videoTitle)
            intent.putExtra(ARGS_VIDEO_PATH, videoPath)
            intent.putExtra(ARGS_THUNDER_TASK_ID, thunderTaskId)
            context.startActivity(intent)
        }
    }
}