package com.seiko.core.util

import java.io.IOException
import com.seiko.core.data.Result


/**
 * Wrap a suspending API [call] in try/catch. In case an exception is thrown, a [Result.Error] is
 * created based on the [errorMessage].
 */
suspend fun <T : Any> safeApiCall(call: suspend () -> Result<T>, errorMessage: String): Result<T> {
    return try {
        call()
    } catch (e: Exception) {
        Result.Error(e)
//        // An exception was thrown when calling the API so we're converting this to an IOException
//        Result.Error(IOException(errorMessage + e.message))
    }
}
