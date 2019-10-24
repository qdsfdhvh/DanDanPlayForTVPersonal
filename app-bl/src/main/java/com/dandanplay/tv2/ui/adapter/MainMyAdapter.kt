package com.dandanplay.tv2.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dandanplay.tv2.R
import com.dandanplay.tv2.bean.MyBean
import com.dandanplay.tv2.utils.setDrawableRes
import com.dandanplay.tv2.widget.ForceListener
import kotlinx.android.synthetic.main.recview_item_main_my.*

class MainMyAdapter(context: Context, private val items: Array<MyBean>) : BaseAdapter(), View.OnFocusChangeListener {

    private val inflater = LayoutInflater.from(context)
    private var mListener: OnItemClickListener? = null

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = inflater.inflate(R.layout.recview_item_main_my, parent, false)
        return MyViewHolder(view)
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (v is ForceListener) {
            v.setUpEnabled(hasFocus)
        }
    }

    inner class MyViewHolder(view: View): BaseViewHolder(view) {
        init {
            (itemView as? ForceListener)?.setUpDrawable(R.drawable.shadow_item_main)
            itemView.onFocusChangeListener = this@MainMyAdapter
            itemView.setOnClickListener {
                mListener?.onItemClick(itemView, items[adapterPosition].id)
            }
        }

        override fun bind(position: Int) {
            val item = items[position]
            name.text = item.name
            img_icon.setDrawableRes(item.image)
            frame.setBackgroundResource(item.background)
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(itemView: View, id: Int)
    }

}