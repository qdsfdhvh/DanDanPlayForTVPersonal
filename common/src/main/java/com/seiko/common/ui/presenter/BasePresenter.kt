package com.seiko.common.ui.presenter

import android.os.Bundle
import androidx.leanback.widget.Presenter

abstract class BasePresenter : Presenter() {

    override fun onBindViewHolder(holder: ViewHolder, item: Any?, payloads: MutableList<Any>?) {
        if (payloads.isNullOrEmpty()) {
            onBindViewHolder(holder, item)
        } else {
            val bundle = payloads[0]
            if (bundle is Bundle) {
                onPayload(holder, bundle)
            }
        }
    }

    open fun onPayload(holder: ViewHolder, bundle: Bundle) {

    }

    override fun onUnbindViewHolder(holder: ViewHolder?) {

    }
}