package com.seiko.tv.ui.bangumi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import com.seiko.tv.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BangumiFavoriteActivity : FragmentActivity(R.layout.activity_container) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportFragmentManager.findFragmentByTag(FRAGMENT_TAG) == null) {
            val fragment = BangumiFavoriteFragment.newInstance()
            supportFragmentManager.commit {
                add(R.id.container, fragment, FRAGMENT_TAG)
            }
        }
    }

    companion object {
        private const val FRAGMENT_TAG = "FRAGMENT_TAG"

        fun launch(activity: Activity) {
            val intent = Intent(activity, BangumiFavoriteActivity::class.java)
            activity.startActivity(intent)
        }
    }
}