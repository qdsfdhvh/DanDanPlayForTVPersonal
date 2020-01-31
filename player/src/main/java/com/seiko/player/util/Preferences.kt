package com.seiko.player.util

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

import org.json.JSONArray
import org.json.JSONException

object Preferences {
    const val TAG = "VLC/UiTools/Preferences"

    fun getFloatArray(pref: SharedPreferences, key: String): FloatArray? {
        var array: FloatArray? = null
        val s = pref.getString(key, null)
        if (s != null) {
            try {
                val json = JSONArray(s)
                array = FloatArray(json.length())
                for (i in array.indices)
                    array[i] = json.getDouble(i).toFloat()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
        return array
    }

    fun putFloatArray(editor: Editor, key: String, array: FloatArray) {
        try {
            val json = JSONArray()
            for (f in array)
                json.put(f.toDouble())
            editor.putString(key, json.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

}
