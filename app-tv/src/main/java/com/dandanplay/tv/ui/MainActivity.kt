package com.dandanplay.tv.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.blankj.utilcode.util.LogUtils
import com.dandanplay.tv.R

class MainActivity : FragmentActivity() {

    /**
     * PS: Navigation在返回时，Fragment的View会重新绘制，需要注意。
     */
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = this.findNavController(R.id.myNavHostFragment)
    }

    /**
     * 当软键盘弹出时，关闭软键盘。
     */
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_BACK) {
            if (isSoftInputMethodShowing()) {
                hideSoftInput()
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }
}


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