package com.seiko.torrent.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.LogUtils
import com.seiko.common.eventbus.EventBusScope
import com.seiko.common.router.Routes
import com.seiko.torrent.R
import com.seiko.torrent.model.PostEvent
import com.seiko.torrent.ui.main.MainFragmentDirections
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = Routes.Torrent.PATH)
class TorrentMainActivity : AppCompatActivity(R.layout.torrent_activity_main) {

    @Autowired(name = Routes.Torrent.KEY_TORRENT_PATH)
    @JvmField
    var source: Uri? = null

    /**
     * 添加状态
     */
    private var addState = STATE_NULL

    /**
     * PS: Navigation在返回时，Fragment的View会重新绘制。
     */
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        navController = findNavController(R.id.myNavHostFragment)
        if (source != null) {
            addState = STATE_SOURCE_INTENT
            navController.navigate(
                MainFragmentDirections.actionMainFragmentToAddTorrentFragment(source!!)
            )
        }
        EventBusScope.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusScope.unRegister(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // 从app-tv跳转过来，又没有添加种子任务，直接退出Torrent
        if (addState == STATE_SOURCE_INTENT) {
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceive(event: PostEvent) {
        when(event) {
            is PostEvent.TorrentAdded -> {
                if (source != null) {
                    val data = Intent()
                    data.putExtra(Routes.Torrent.RESULT_KEY_ADD_SUCCESS, true)
                    data.putExtra(Routes.Torrent.RESULT_KEY_ADD_HASH, event.torrent.hash)
                    setResult(RESULT_OK, data)
                    addState = STATE_SUCCESS
                }
            }
        }
    }

    companion object {
        private const val STATE_NULL = 0
        private const val STATE_SOURCE_INTENT= 1
        private const val STATE_SUCCESS = 2
    }
}