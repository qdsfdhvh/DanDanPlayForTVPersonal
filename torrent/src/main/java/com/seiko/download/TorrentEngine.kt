package com.seiko.download

import com.frostwire.jlibtorrent.AlertListener
import com.frostwire.jlibtorrent.SessionManager
import com.frostwire.jlibtorrent.SettingsPack
import com.frostwire.jlibtorrent.alerts.Alert
import com.frostwire.jlibtorrent.alerts.AlertType
import com.frostwire.jlibtorrent.alerts.SessionErrorAlert
import com.seiko.download.task.TorrentTask
import com.seiko.download.task.TorrentTaskManager
import com.seiko.download.utils.toTorrentInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class TorrentEngine : SessionManager(), AlertListener {

    private val downloadScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    // 下载引擎中的任务集合. key: hash, value: task
    private val mTaskMap: ConcurrentHashMap<String, TorrentTask> = ConcurrentHashMap()
    // 临时储存新增的Torrent任务
    private val mNewTaskMap: ConcurrentHashMap<String, TorrentTask> = ConcurrentHashMap()
    // 新增任务的集合
    private val mNewTaskQueue: Queue<TorrentTaskManager> = LinkedList()

    /**
     * 初始化下载配置
     */
    private fun getEngineSetting(): SettingsPack {
        return SettingsPack()
//            .setString(settings_pack.string_types.dht_bootstrap_nodes.swigValue(),
//                getDhtBootstrapNodeString()) // 路由表
            .downloadRateLimit(0) // 下载速度限制
            .uploadRateLimit(0)   // 上传速度限制
            .connectionsLimit(200) // 连接数量限制
            .activeDhtLimit(88)    // dht限制
            .anonymousMode(false)  // 是否为匿名模式
            .activeLimit(5)        // 最大任务数量
    }

    /**
     * 更新配置 任务数/下载速度限制
     */
    fun updateSetting() {
        applySettings(SettingsPack()
            .downloadRateLimit(0)
            .activeLimit(5))
        if (swig() != null) {
            // 储存session
        }
    }

    fun download(task: TorrentTaskManager) {
        downloadScope.launch {
//            addListener(object : AlertListener {
//                override fun types(): IntArray {
//                    return this@TorrentEngine.types()
//                }
//
//                override fun alert(p0: Alert<*>?) {
//                    this@TorrentEngine.alert(p0)
//                }
//            })
            addListener(this@TorrentEngine)
        }
//        val info = task.torrentPath.toTorrentInfo()
//        if (task.priorityList.size != info.numFiles()) {
//            Log.e("TAG", "添加任务失败")
//            return
//        }
//
//        if (mTaskMap.containsKey(task.tag())) {
//            return
//        }
//        mTaskMap[task.tag()] = task

//        download()
    }

    override fun types(): IntArray {
        return ACCEPT_ALERT_TYPE
    }

    override fun alert(alert: Alert<*>?) {
        when(alert?.type()) {
            AlertType.ADD_TORRENT -> {

            }
            AlertType.TORRENT_REMOVED -> {

            }
            AlertType.SESSION_ERROR -> {
                val sessionErrorAlert = alert as SessionErrorAlert
                val errorCode = sessionErrorAlert.error()

            }
        }
    }

    /**
     * 单例
     */
    private object Holder {
        val INSTANCE = TorrentEngine()
    }

    companion object {
        private const val TAG = "TorrentEngine"

        private val ACCEPT_ALERT_TYPE = intArrayOf(
            AlertType.ADD_TORRENT.swig(),
            AlertType.TORRENT_REMOVED.swig(),
            AlertType.SESSION_ERROR.swig()
        )

        fun getIntance() = Holder.INSTANCE
    }
}