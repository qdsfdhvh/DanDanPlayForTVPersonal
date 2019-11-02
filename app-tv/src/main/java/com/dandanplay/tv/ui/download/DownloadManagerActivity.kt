package com.dandanplay.tv.ui.download

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.dandanplay.tv.R
import com.dandanplay.tv.service.TorrentService

class DownloadManagerActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_manager)
        TorrentService.start(this)
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
        TorrentService.stop(this)
    }

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, DownloadManagerActivity::class.java)
            context.startActivity(intent)
        }
    }

}