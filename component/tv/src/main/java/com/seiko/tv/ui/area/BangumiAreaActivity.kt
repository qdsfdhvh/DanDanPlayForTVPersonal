package com.seiko.tv.ui.area

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.seiko.common.base.BaseOneFragmentActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BangumiAreaActivity : BaseOneFragmentActivity() {

    override fun onCreateFragment(): Fragment {
        return BangumiAreaFragmentV2.newInstance()
    }

    companion object {
        fun launch(activity: Activity) {
            val intent = Intent(activity, BangumiAreaActivity::class.java)
            activity.startActivity(intent)
        }
    }

}