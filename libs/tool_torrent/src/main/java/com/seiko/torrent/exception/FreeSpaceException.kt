package com.seiko.torrent.exception

class FreeSpaceException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(e: Exception) : super(e.message) {
        e.stackTrace = e.stackTrace
    }
}