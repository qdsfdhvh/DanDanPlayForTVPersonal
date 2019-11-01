package com.seiko.download

object TrackerManger {

    //tracker列表
    private val trackers = ArrayList<String>(100)


    fun addTracker(tracker: String) {
        trackers.add(tracker)
    }

    fun addTrackers(trackers: List<String>) {
        this.trackers.addAll(trackers)
    }

    fun clear() {
        trackers.clear()
    }

    fun getTrackers(): List<String> {
        return trackers
    }
}