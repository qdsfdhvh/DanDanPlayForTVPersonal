package com.seiko.player.util

open class SingletonHolder<T>(creator: () -> T) {
    private var creator: (() -> T)? = creator
    @Volatile var single: T? = null

    fun getInstance() = single ?: synchronized(this) {
        val i2 = single
        if (i2 != null) i2
        else {
            val created = creator!!()
            single = created
            creator = null
            created
        }
    }
}