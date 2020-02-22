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

class BangumiDetailsActivity : FragmentActivity(R.layout.activity_bangumi_detials) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.sharedElementEnterTransition = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP) // 进入
            window.sharedElementReturnTransition = DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP) // 返回
        }
        if (supportFragmentManager.findFragmentByTag(BangumiDetailsFragment.TAG) == null) {
            val fragment = BangumiDetailsFragment.newInstance(intent.extras!!)
            supportFragmentManager.commit {
                add(R.id.container, fragment, BangumiDetailsFragment.TAG)
            }
        }
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