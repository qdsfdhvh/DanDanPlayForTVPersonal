package com.dandanplay.tv.extensions

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

/**
 * 软键盘是否弹出
 */
fun Activity.isSoftInputMethodShowing(): Boolean {
    //获取当前屏幕内容的高度
    val screenHeight = window.decorView.height
    //获取View可见区域的bottom
    val rect = android.graphics.Rect()
    window.decorView.getWindowVisibleDisplayFrame(rect)

    val value = screenHeight - rect.bottom
    return value > 48
}

/**
 * 关闭软键盘
 */
fun Activity.hideSoftInput() {
    val view = window.peekDecorView()
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
    imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
}