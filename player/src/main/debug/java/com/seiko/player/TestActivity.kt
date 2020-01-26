package com.seiko.player

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.seiko.player.ui.PlayerManagerActivity

class TestActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, PlayerManagerActivity::class.java)
        startActivity(intent)
    }

}