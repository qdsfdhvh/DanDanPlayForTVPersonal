package com.dandanplay.tv2.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dandanplay.tv2.R
import com.dandanplay.tv2.bean.MyBean
import com.dandanplay.tv2.utils.setDrawableRes
import com.seiko.domain.entities.BangumiIntro
import kotlinx.android.synthetic.main.recview_item_main_bangumi_left.view.*
import kotlinx.android.synthetic.main.recview_item_main_bangumi_right.view.*

private const val ITEM_TYPE_LEFT = 1000
private const val ITEM_TYPE_RIGHT = 2000

class BangumiAdapter(context: Context) : BaseAdapter() {

    private val inflater = LayoutInflater.from(context)
    private var mListener: OnItemClickListener? = null

    var items: List<Any> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position]) {
            is BangumiIntro -> ITEM_TYPE_RIGHT
            else -> ITEM_TYPE_LEFT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view: View
        return when(viewType) {
            ITEM_TYPE_LEFT -> {
                view = inflater.inflate(R.layout.recview_item_main_bangumi_left, parent, false)
                LeftViewHolder(view)
            }
            ITEM_TYPE_RIGHT -> {
                view = inflater.inflate(R.layout.recview_item_main_bangumi_right, parent, false)
                RightViewHolder(view)
            }
            else -> throw RuntimeException("Unknown ItemViewType = $viewType.")
        }
    }

    inner class LeftViewHolder(view: View) : BaseViewHolder(view) {
        override fun bind(position: Int) {
            val item = items[position]
            if (item is MyBean) {
                itemView.logo.setDrawableRes(item.image)
                itemView.name.text = item.name
                itemView.frame.setBackgroundResource(item.background)
            }
        }
    }

    inner class RightViewHolder(view: View) : BaseViewHolder(view) {

        init {
            itemView.setOnClickListener {
                mListener?.onItemClick(itemView, items[adapterPosition] as? BangumiIntro ?: return@setOnClickListener)
            }
        }

        override fun bind(position: Int) {
            val item = items[position]
            if (item is BangumiIntro) {
                itemView.img.setImageURI(item.imageUrl)
                itemView.title.text = item.animeTitle
            }
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(itemView: View, intro: BangumiIntro)
    }

    fun getLayoutManager(context: Context, spaceCount: Int): RecyclerView.LayoutManager {
        val layoutManager = GridLayoutManager(context, spaceCount, RecyclerView.HORIZONTAL, false)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val itemType = getItemViewType(position)
                return if(itemType == ITEM_TYPE_LEFT) 1 else spaceCount
            }
        }
        return layoutManager
    }

//    fun getLayoutManager(context: Context, count: Int): RecyclerView.LayoutManager {
//        return object : ModuleLayoutManager(count, LinearLayoutManager.HORIZONTAL) {
//            override fun getItemStartIndex(position: Int): Int {
//                val itemType = getItemViewType(position)
//                return if (itemType == ITEM_TYPE_LEFT) position else count * (position - 3)
//            }
//
//            override fun getItemRowSize(position: Int): Int {
//                val itemType = getItemViewType(position)
//                return if (itemType == ITEM_TYPE_LEFT) 1 else count
//            }
//
//            override fun getItemColumnSize(position: Int): Int {
//                return 1
//            }
//
//            override fun getColumnSpacing(): Int {
//                return context.resources.getDimensionPixelSize(R.dimen.px_12)
//            }
//
//            override fun getRowSpacing(): Int {
//                return context.resources.getDimensionPixelSize(R.dimen.px_12)
//            }
//
//        }
//    }

}