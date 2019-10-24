package com.dandanplay.tv2.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import com.dandanplay.tv2.R
import com.dandanplay.tv2.ui.base.BaseActivity
import com.dandanplay.tv2.ui.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initViews()
    }

    private fun initViews() {
        GlobalScope.launch(Dispatchers.Main) {
            delay(400)
            launchActivity()
        }
    }

    private fun launchActivity() {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit)
        this.finish()
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.keyCode == KeyEvent.KEYCODE_BACK) {
            return true
        }
        return super.dispatchKeyEvent(event)
    }

}