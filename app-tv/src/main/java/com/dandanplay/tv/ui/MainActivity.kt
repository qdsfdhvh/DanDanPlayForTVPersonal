package com.dandanplay.tv.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
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

}