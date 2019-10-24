package com.dandanplay.tv2.helper

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dandanplay.tv2.R

class MainPaddingDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val dividerTop = context.resources.getDimensionPixelSize(R.dimen.px_66)
    private val dividerHeight = context.resources.getDimensionPixelSize(R.dimen.px_24)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
//            val position = parent.getChildAdapterPosition(view)
//            val viewType = getItemViewType(position)
//            if (viewType == ITEM_TYPE_RIGHT) {
        outRect.top = dividerTop
        outRect.bottom = dividerHeight
        outRect.left = dividerHeight
        outRect.right = dividerHeight
//            }
    }
}