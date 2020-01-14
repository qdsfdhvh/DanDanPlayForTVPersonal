/*
 * Copyright (C) 2016 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of LibreTorrent.
 *
 * LibreTorrent is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LibreTorrent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LibreTorrent.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.seiko.torrent.ui.main

import android.util.SparseBooleanArray
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList

/*
 * The base class for adapters with the ability to select items.
 */
abstract class SelectableAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    private val selectedItems = SparseBooleanArray()

    fun isSelected(position: Int): Boolean {
        return getSelectedItems().contains(position)
    }

    fun toggleSelection(position: Int) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position)
        } else {
            selectedItems.put(position, true)
        }
        notifyItemChanged(position)
    }

    fun clearSelection() {
        val selection = getSelectedItems()
        selectedItems.clear()
        for (position in selection) {
            notifyItemChanged(position)
        }
    }

    fun getSelectedItemCount(): Int {
        return selectedItems.size()
    }

    fun getSelectedItems(): List<Int> {
        val size = selectedItems.size()
        val items = ArrayList<Int>(size)
        for (i in 0 until size) {
            items.add(selectedItems.keyAt(i))
        }
        return items
    }

    fun setSelectedItems(items: List<Int>) {
        for (position in items) {
            selectedItems.put(position, true)
        }
    }
}