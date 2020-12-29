package com.seiko.tv.ui.bangumi

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.seiko.common.base.BaseOneFragmentActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BangumiTimeLineActivity : BaseOneFragmentActivity() {

    override fun onCreateFragment(): Fragment {
        return BangumiTimeLineFragment.newInstance()
    }

    companion object {
        fun launch(activity: Activity) {
            val intent = Intent(activity, BangumiTimeLineActivity::class.java)
            activity.startActivity(intent)
        }
    }
}