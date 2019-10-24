package com.dandanplay.tv2.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dandanplay.tv2.R
import kotlinx.android.synthetic.main.recview_item_main_title_icon.*
import kotlinx.android.synthetic.main.recview_item_main_title_text.*

private const val ITEM_TYPE_SEARCH = 1000
private const val ITEM_TYPE_TITLE = 2000
private const val ITEM_TYPE_SETTING = 3000

private const val PREF_TITLE_COUNT = 1     // Title前其他Item的数量
private const val MAX_OTHER_ITEM_COUNT = 2 // Title外其他Item的总数

class MainTitleAdapter(context: Context, private val items: Array<String>) : BaseAdapter(), View.OnFocusChangeListener {

    private val inflater = LayoutInflater.from(context)

    override fun getItemCount(): Int = items.size + MAX_OTHER_ITEM_COUNT

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> ITEM_TYPE_SEARCH
            items.size + MAX_OTHER_ITEM_COUNT - 1 -> ITEM_TYPE_SETTING
            else -> ITEM_TYPE_TITLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view: View
        return when(viewType) {
            ITEM_TYPE_TITLE -> {
                view = inflater.inflate(R.layout.recview_item_main_title_text, parent, false)
                TitleViewHolder(view)
            }
            ITEM_TYPE_SEARCH -> {
                view = inflater.inflate(R.layout.recview_item_main_title_icon, parent, false)
                SearchViewHolder(view)
            }
            ITEM_TYPE_SETTING -> {
                view = inflater.inflate(R.layout.recview_item_main_title_icon, parent, false)
                SettingViewHolder(view)
            }
            else -> throw RuntimeException("Unknown ItemViewType = $viewType.")
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        v?.isSelected = hasFocus
    }

    inner class SearchViewHolder(view: View): BaseViewHolder(view) {
        init {
            icon.setImageResource(R.drawable.selector_main_search)
            itemView.setOnClickListener {
                mListener?.onSearchClick()
            }
            itemView.onFocusChangeListener = this@MainTitleAdapter
        }
    }

    inner class SettingViewHolder(view: View): BaseViewHolder(view) {
        init {
            icon.setImageResource(R.drawable.selector_main_setting)
            itemView.setOnClickListener {
                mListener?.onSettingClick()
            }
            itemView.onFocusChangeListener = this@MainTitleAdapter
        }
    }

    inner class TitleViewHolder(view: View): BaseViewHolder(view) {
        init {
            itemView.setOnFocusChangeListener { _, hasFocus ->
                itemView.isSelected = hasFocus
                if (hasFocus) {
                    mListener?.onTitleFocus(getViewPagerPosition(adapterPosition))
                }
            }
        }

        override fun bind(position: Int) {
            title.text = items[getViewPagerPosition(position)]
        }
    }

    private var mListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

    interface OnItemClickListener {
        fun onSearchClick()
        fun onSettingClick()
        fun onTitleFocus(position: Int)
    }

    /**
     * 实际RecyclerAdapter的位置
     */
    fun getTruePosition(viewPagerPosition: Int): Int {
        return viewPagerPosition + PREF_TITLE_COUNT
    }

    /**
     * 对于ViewPager来说的位置
     */
    private fun getViewPagerPosition(truePosition: Int): Int {
        return truePosition - PREF_TITLE_COUNT
    }

}

