package com.seiko.tv.ui.bangumi

//import android.app.Activity
//import android.content.Intent
//import android.os.Bundle
//import android.view.View
//import androidx.core.app.ActivityOptionsCompat
//import androidx.fragment.app.commit
//import com.seiko.common.base.BaseActivity
//import com.seiko.tv.R
//import com.seiko.tv.data.model.HomeImageBean
//import com.seiko.tv.util.TRANSITION_NAME
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class BangumiDetailsActivity : BaseActivity(R.layout.activity_container) {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        if (supportFragmentManager.findFragmentByTag(BangumiDetailsFragment.TAG) == null) {
//            val fragment = BangumiDetailsFragment.newInstance(intent.extras!!)
//            supportFragmentManager.commit {
//                add(R.id.container, fragment, BangumiDetailsFragment.TAG)
//            }
//        }
//    }
//
//    companion object {
//        fun launch(activity: Activity, item: HomeImageBean, imageView: View) {
//            launch(activity, item.animeId, item.imageUrl, imageView)
//        }
//
//        fun launch(activity: Activity, animeId: Long, imageUrl: String, imageView: View) {
//            val intent = Intent(activity, BangumiDetailsActivity::class.java)
//            val bundle = BangumiDetailsFragmentArgs(animeId, imageUrl).toBundle()
//            intent.putExtras(bundle)
//            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                activity, imageView, TRANSITION_NAME).toBundle()
//            activity.startActivity(intent, options)
//        }
//    }
//}