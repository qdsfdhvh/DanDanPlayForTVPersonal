package com.seiko.player.ui

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.seiko.player.R
import org.videolan.libvlc.util.VLCUtil

class CompatErrorActivity : Activity() {

    /**
     * Simple friendly activity to tell the user something's wrong.
     *
     * Intent parameters (all optional):
     * runtimeError (bool) - Set to true if you want to show a runtime error
     * (defaults to a compatibility error)
     * message (string) - the more detailed problem
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_activity_not_compatible)

        var errorMsg = VLCUtil.getErrorMsg()
        if (intent.getBooleanExtra("runtimeError", false))
            if (intent.getStringExtra("message") != null) {
                errorMsg = intent.getStringExtra("message")
                val tvo = findViewById<View>(R.id.message) as TextView
                tvo.setText(R.string.error_problem)
            }

        val tv = findViewById<View>(R.id.errormsg) as TextView
        tv.text = String.format("%s\n%s", resources.getString(R.string.error_message_is), errorMsg)
    }

    companion object {
        const val TAG = "VLC/CompatErrorActivity"
    }
}
