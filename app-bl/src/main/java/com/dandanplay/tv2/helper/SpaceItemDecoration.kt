package com.dandanplay.tv2.helper

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(private val left: Int,
                          private val top: Int,
                          private val right: Int,
                          private val bottom: Int): RecyclerView.ItemDecoration() {

    constructor(space: Int): this(space, space, space, space)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = top
        outRect.bottom = bottom
        outRect.left = left
        outRect.right = right
    }
}