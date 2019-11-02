package com.seiko.download

import com.seiko.download.recorder.TaskRecorder
import com.seiko.download.task.TorrentTask

/**
 * TaskManager
 */
object TorrentTaskManagerPool {

    private val map = mutableMapOf<TorrentTask, TorrentTaskManager>()


    private fun add(task: TorrentTask, taskManager: TorrentTaskManager) {
        map[task] = taskManager
    }

    private fun get(task: TorrentTask): TorrentTaskManager? {
        return map[task]
    }

    private fun remove(task: TorrentTask) {
        map.remove(task)
    }

    fun obtain(task: TorrentTask,
               taskRecorder: TaskRecorder?): TorrentTaskManager {
        if (get(task) == null) {
            synchronized(this) {
                if (get(task) == null) {
                    val manager = TorrentTaskManager(
                        task = task,
                        taskRecorder = taskRecorder
                    )
                    add(task, manager)
                }
            }
        }
        return get(task)!!
    }
}