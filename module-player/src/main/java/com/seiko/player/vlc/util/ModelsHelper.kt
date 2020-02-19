package com.seiko.player.vlc.util

import android.content.Context
import com.seiko.player.R
import com.seiko.player.vlc.extensions.*
import com.seiko.player.vlc.extensions.getDiscNumber
import com.seiko.player.vlc.extensions.getLength
import com.seiko.player.vlc.extensions.getYear
import com.seiko.player.vlc.extensions.lengthToCategory
import org.videolan.medialibrary.interfaces.Medialibrary
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import org.videolan.medialibrary.media.MediaLibraryItem
import java.util.*

object ModelsHelper {
    fun getHeader(context: Context?,
                  sort: Int,
                  item: MediaLibraryItem?,
                  aboveItem: MediaLibraryItem?,
                  forceByDiscs: Boolean = false
    ) = if (context !== null && item != null) if (forceByDiscs) {
        val disc = item.getDiscNumber()
        if (aboveItem == null) disc
        else {
            val previousDisc = aboveItem.getDiscNumber()
            disc.takeIf { it != previousDisc }
        }
    } else when (sort) {
        Medialibrary.SORT_DEFAULT,
        Medialibrary.SORT_FILENAME,
        Medialibrary.SORT_ALPHA -> {
            val letter = if (item.title.isEmpty()
                || !Character.isLetter(item.title[0])
                || item.isSpecialItem()) {
                "#"
            } else {
                item.title.substring(0, 1).toUpperCase(Locale.US)
            }
            if (aboveItem == null) {
                letter
            } else {
                val previous = if (aboveItem.title.isEmpty()
                    || !Character.isLetter(aboveItem.title[0])
                    || aboveItem.isSpecialItem()) {
                    "#"
                } else {
                    aboveItem.title.substring(0, 1).toUpperCase(Locale.US)
                }
                letter.takeIf { it != previous }
            }
        }
        Medialibrary.SORT_DURATION -> {
            val length = item.getLength()
            val lengthCategory = length.lengthToCategory()
            if (aboveItem == null) lengthCategory
            else {
                val previous = aboveItem.getLength().lengthToCategory()
                lengthCategory.takeIf { it != previous }
            }
        }
        Medialibrary.SORT_RELEASEDATE -> {
            val year = item.getYear()
            if (aboveItem == null) year
            else {
                val previous = aboveItem.getYear()
                year.takeIf { it != previous }
            }
        }
        Medialibrary.SORT_LASTMODIFICATIONDATE -> {
            val timestamp = (item as MediaWrapper).lastModified
            val category =
                getTimeCategory(
                    timestamp
                )
            if (aboveItem == null) getTimeCategoryString(
                context,
                category
            )
            else {
                val prevCat =
                    getTimeCategory((aboveItem as MediaWrapper).lastModified)
                if (prevCat != category) getTimeCategoryString(
                    context,
                    category
                ) else null
            }
        }
        Medialibrary.SORT_ARTIST -> {
            val artist = (item as MediaWrapper).artist ?: ""
            if (aboveItem == null) artist
            else {
                val previous = (aboveItem as MediaWrapper).artist ?: ""
                artist.takeIf { it != previous }
            }
        }
        Medialibrary.SORT_ALBUM -> {
            val album = (item as MediaWrapper).album ?: ""
            if (aboveItem == null) album
            else {
                val previous = (aboveItem as MediaWrapper).album ?: ""
                album.takeIf { it != previous }
            }
        }
        else -> null
    } else null

    private const val LENGTH_WEEK = 7 * 24 * 60 * 60
    private const val LENGTH_MONTH = 30 * LENGTH_WEEK
    private const val LENGTH_YEAR = 52 * LENGTH_WEEK
    private const val LENGTH_2_YEAR = 2 * LENGTH_YEAR

    private fun getTimeCategory(timestamp: Long): Int {
        val delta = (System.currentTimeMillis() / 1000L) - timestamp
        return when {
            delta < LENGTH_WEEK -> 0
            delta < LENGTH_MONTH -> 1
            delta < LENGTH_YEAR -> 2
            delta < LENGTH_2_YEAR -> 3
            else -> 4
        }
    }

    private fun getTimeCategoryString(context: Context, cat: Int) = when (cat) {
        0 -> context.getString(R.string.time_category_new)
        1 -> context.getString(R.string.time_category_current_month)
        2 -> context.getString(R.string.time_category_current_year)
        3 -> context.getString(R.string.time_category_last_year)
        else -> context.getString(R.string.time_category_older)
    }
}

