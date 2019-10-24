package com.dandanplay.tv2.ui.adapter

//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.leanback.widget.Presenter
//import androidx.leanback.widget.PresenterSelector
//import com.dandanplay.tv.R
//import kotlinx.android.synthetic.main.recview_item_main_title_icon.*
//import kotlinx.android.synthetic.main.recview_item_main_title_text.*
//
//class MainIconPresenter : BasePresenter() {
//
//    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.recview_item_main_title_icon, parent, false)
//        return ItemViewHolder(view)
//    }
//
//    class ItemViewHolder(view: View) : BaseViewHolder(view) {
//        override fun bind(item: Any?) {
//            if (item is Int) {
//                icon.setImageResource(item)
//            }
//        }
//    }
//
//}
//
//class MainTitlePresenter : BasePresenter() {
//
//    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.recview_item_main_title_text, parent, false)
//        return ItemViewHolder(view)
//    }
//
//    class ItemViewHolder(view: View) : BaseViewHolder(view) {
//        override fun bind(item: Any?) {
//            if (item is String) {
//                title.text = item
//            }
//        }
//    }
//
//}
//
//class MainPresenterSelector : PresenterSelector() {
//
//    private val iconPresenter = MainIconPresenter()
//    private val titlePresenter = MainTitlePresenter()
//
//    override fun getPresenter(item: Any?): Presenter {
//        return if (item is String) {
//            titlePresenter
//        } else {
//            iconPresenter
//        }
//    }
//
//}

