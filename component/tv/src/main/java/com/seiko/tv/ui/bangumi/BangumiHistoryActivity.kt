package com.seiko.tv.ui.bangumi

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.seiko.common.base.BaseOneFragmentActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BangumiHistoryActivity : BaseOneFragmentActivity() {

    override fun onCreateFragment(): Fragment {
        return BangumiHistoryFragment.newInstance()
    }

    companion object {
        fun launch(activity: Activity) {
            val intent = Intent(activity, BangumiHistoryActivity::class.java)
            activity.startActivity(intent)
        }
    }
}