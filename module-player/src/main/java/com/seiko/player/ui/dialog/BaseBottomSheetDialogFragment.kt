package com.seiko.player.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.seiko.player.R

abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransBottomSheetDialogStyle)
    }

    override fun onStart() {
        super.onStart()
        dialog?.run {
            window?.run {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setLayout(
                    resources.getDimensionPixelSize(R.dimen.default_context_width),
                    ViewGroup.LayoutParams.WRAP_CONTENT)
                findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)?.let {
                    val bsb = BottomSheetBehavior.from(it)
                    if (bsb.state == BottomSheetBehavior.STATE_COLLAPSED) {
                        BottomSheetBehavior.STATE_EXPANDED
                    }
                }
            }
            findViewById<View>(R.id.touch_outside)?.isFocusable = false
            findViewById<View>(R.id.touch_outside)?.isFocusableInTouchMode = false
        }
    }

}