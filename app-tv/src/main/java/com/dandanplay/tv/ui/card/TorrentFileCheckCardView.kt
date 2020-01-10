package com.dandanplay.tv.ui.card

import android.content.Context
import com.dandanplay.tv.R
import com.dandanplay.tv.extensions.formatSize
import com.seiko.core.util.TYPE_AUDIO
import com.seiko.core.util.TYPE_PICTURE
import com.seiko.core.util.TYPE_SUBTITLE
import com.seiko.core.util.TYPE_VIDEO
import com.seiko.core.model.TorrentCheckBean
import kotlinx.android.synthetic.main.item_torrent.view.*

class TorrentFileCheckCardView(context: Context) : AbsCardView<TorrentCheckBean>(context) {

    override fun getLayoutId(): Int {
        return R.layout.item_torrent
    }

    override fun bind(item: TorrentCheckBean) {
        title.text = item.name
        size.text = item.size.formatSize()
        val mipmapRes = when(item.type) {
            TYPE_VIDEO -> {
                R.mipmap.file_video
            }
            TYPE_AUDIO -> {
                R.mipmap.file_music
            }
            TYPE_PICTURE -> {
                R.mipmap.file_pic
            }
            TYPE_SUBTITLE -> {
                R.mipmap.file_txt
            }
            else -> {
                R.mipmap.file_unknown
            }
        }
        type.setImageResource(mipmapRes)
    }

}