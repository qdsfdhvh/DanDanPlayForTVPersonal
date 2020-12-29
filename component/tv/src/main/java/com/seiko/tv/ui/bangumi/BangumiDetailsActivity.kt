package com.seiko.tv.ui.bangumi

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.seiko.common.base.BaseOneFragmentActivity
import com.seiko.tv.data.model.HomeImageBean
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BangumiDetailsActivity : BaseOneFragmentActivity() {

    override fun onCreateFragment(): Fragment {
        return BangumiDetailsFragment.newInstance(intent.extras!!)
    }

    companion object {
        fun launch(activity: Activity, item: HomeImageBean, imageView: View) {
            launch(activity, item.animeId, item.imageUrl, imageView)
        }

        fun launch(activity: Activity, animeId: Long, imageUrl: String, imageView: View) {
            val intent = Intent(activity, BangumiDetailsActivity::class.java)
            intent.putExtra(BangumiDetailsFragment.ARGS_ANIME_ID, animeId)
            intent.putExtra(BangumiDetailsFragment.ARGS_ANIME_IMAGE_URL, imageUrl)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity, imageView, BangumiDetailsFragment.TRANSITION_NAME
            ).toBundle()
            activity.startActivity(intent, options)
        }
    }
}