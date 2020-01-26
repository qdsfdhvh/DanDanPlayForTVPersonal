package com.seiko.player.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes
import com.seiko.common.util.toast.toast
import com.seiko.common.ui.dialog.DialogSelectFragment
import com.seiko.player.R
import timber.log.Timber

@Route(path = Routes.Player.PATH)
class PlayerManagerActivity: FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_activity_manager)
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
            toast("播放连接无效。")
            return
        }

        if (supportFragmentManager.findFragmentByTag(ExoPlayerFragment.TAG) == null) {
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.container,
                    ExoPlayerFragment.newInstance(videoTitle, videoPath),
                    ExoPlayerFragment.TAG)
                .commit()
        }
    }

    private fun launchExitDialog() {
        if (supportFragmentManager.findFragmentByTag(DialogSelectFragment.TAG) == null) {
            DialogSelectFragment.Builder()
                .setTitle("你真的确认退出播放吗？")
                .setConfirmText("确认")
                .setCancelText("取消")
                .setConfirmClickListener {
                    ActivityCompat.finishAffinity(this)
                }
                .build()
                .show(supportFragmentManager)
        }
    }

    override fun onStop() {
        super.onStop()
        val thunderTaskId = intent?.getLongExtra(ARGS_THUNDER_TASK_ID, -1L) ?: -1L
        if (thunderTaskId != -1L) {
            Timber.d("停止thunderTaskId=$thunderTaskId。")
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