package com.seiko.tv.ui.bangumi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.commit
import com.seiko.common.base.BaseActivity
import com.seiko.tv.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BangumiHistoryActivity : BaseActivity(R.layout.activity_container) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportFragmentManager.findFragmentByTag(FRAGMENT_TAG) == null) {
            val fragment = BangumiHistoryFragment.newInstance()
            supportFragmentManager.commit {
                add(R.id.container, fragment, FRAGMENT_TAG)
            }
        }
    }

    companion object {
        private const val FRAGMENT_TAG = "FRAGMENT_TAG"

        fun launch(activity: Activity) {
            val intent = Intent(activity, BangumiHistoryActivity::class.java)
            activity.startActivity(intent)
        }
    }
}