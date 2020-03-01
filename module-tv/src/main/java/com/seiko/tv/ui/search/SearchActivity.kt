package com.seiko.tv.ui.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import com.seiko.tv.R
import com.seiko.tv.util.setupSharedElementTransition

class SearchActivity : FragmentActivity(R.layout.activity_container) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupSharedElementTransition()
        if (supportFragmentManager.findFragmentByTag(TAG) == null) {
            val fragment: Fragment = when(intent.getIntExtra(ARGS_TYPE, TYPE_BANGUMI)) {
                TYPE_MAGNET -> SearchMagnetFragment.newInstance(intent.extras!!)
                else -> SearchBangumiFragment.newInstance()
            }
            supportFragmentManager.commit {
                add(R.id.container, fragment, TAG)
            }
        }
    }

    companion object {
        private const val TAG = "SearchActivity"
        private const val ARGS_TYPE = "ARGS_TYPE"

        private const val TYPE_MAGNET = 11
        private const val TYPE_BANGUMI = 22

        fun launchMagnet(activity: Activity, keyword: String, animeId: Long, episodeId: Int) {
            val intent = Intent(activity, SearchActivity::class.java)
            intent.putExtra(SearchMagnetFragment.ARGS_KEYWORD, keyword)
            intent.putExtra(SearchMagnetFragment.ARGS_ANIME_ID, animeId)
            intent.putExtra(SearchMagnetFragment.ARGS_EPISODE_ID, episodeId)
            intent.putExtra(ARGS_TYPE, TYPE_MAGNET)
            activity.startActivity(intent)
        }

        fun launchBangumi(activity: Activity) {
            val intent = Intent(activity, SearchActivity::class.java)
            intent.putExtra(ARGS_TYPE, TYPE_BANGUMI)
            activity.startActivity(intent)
        }
    }
}