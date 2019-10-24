package com.dandanplay.tv2.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.dandanplay.tv2.R
import com.dandanplay.tv2.bean.MyBean
import com.dandanplay.tv2.utils.setDrawableRes
import kotlinx.android.synthetic.main.recview_item_main_bangumi_left.view.*

class MainBangumiLeftAdapter(context: Context,
                             private val items: List<MyBean>,
                             private val helper: LayoutHelper
) : DelegateAdapter.Adapter<MainBangumiLeftAdapter.ItemViewHolder>() {

    private val inflate = LayoutInflater.from(context)

    override fun onCreateLayoutHelper(): LayoutHelper {
        return helper
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = inflate.inflate(R.layout.recview_item_main_bangumi_left, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.frame.setUpDrawable(R.drawable.shadow_item_main)
        holder.itemView.logo.setDrawableRes(item.image)
        holder.itemView.name.text = item.name
        holder.itemView.frame.setBackgroundResource(item.background)
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view)
}