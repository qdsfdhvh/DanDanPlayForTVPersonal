package com.seiko.tv.ui.bangumi

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.view.DraweeTransition
import com.seiko.tv.R
import com.seiko.tv.util.removeWindowInTransitionManager
import com.seiko.tv.util.setupSharedElementTransition

class BangumiDetailsActivity : FragmentActivity(R.layout.activity_container) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupSharedElementTransition()
        if (supportFragmentManager.findFragmentByTag(BangumiDetailsFragment.TAG) == null) {
            val fragment = BangumiDetailsFragment.newInstance(intent.extras!!)
            supportFragmentManager.commit {
                add(R.id.container, fragment, BangumiDetailsFragment.TAG)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeWindowInTransitionManager()
    }

    companion object {
        fun launch(activity: Activity, animeId: Long, imageView: View) {
            val intent = Intent(activity, BangumiDetailsActivity::class.java)
            intent.putExtra(BangumiDetailsFragment.ARGS_ANIME_ID, animeId)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, imageView, BangumiDetailsFragment.TRANSITION_NAME).toBundle()
            activity.startActivity(intent, options)
        }
    }
}