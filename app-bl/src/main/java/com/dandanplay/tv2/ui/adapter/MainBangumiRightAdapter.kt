package com.dandanplay.tv2.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.dandanplay.tv2.R
import com.seiko.domain.entities.BangumiIntro
import kotlinx.android.synthetic.main.recview_item_main_bangumi_right.view.*

class MainBangumiRightAdapter(context: Context,
                              private val items: List<BangumiIntro>,
                              private val layoutHelper: LayoutHelper
) : DelegateAdapter.Adapter<MainBangumiRightAdapter.ItemViewHolder>() {

    private val inflate = LayoutInflater.from(context)


    override fun onCreateLayoutHelper(): LayoutHelper {
        return layoutHelper
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = inflate.inflate(R.layout.recview_item_main_bangumi_right, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.draw.setUpDrawable(R.drawable.shadow_item_main)
        holder.itemView.img.setImageURI(item.imageUrl)
        holder.itemView.title.text = item.animeTitle
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view)
}