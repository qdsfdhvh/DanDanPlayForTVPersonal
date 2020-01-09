package com.dandanplay.tv.ui.download

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.dandanplay.tv.R

class DownloadManagerActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_manager)

        if (supportFragmentManager.findFragmentByTag(DownloadListFragment.TAG) == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container,
                    DownloadListFragment.newInstance(),
                    DownloadListFragment.TAG)
                .commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, DownloadManagerActivity::class.java)
            context.startActivity(intent)
        }
    }

}