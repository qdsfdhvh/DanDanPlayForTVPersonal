package com.dandanplay.tv.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.dandanplay.tv.R
import com.dandanplay.tv.bean.PostEvent
import com.dandanplay.tv.ui.base.*
import com.dandanplay.tv.ui.dialog.ExitDialogFragment
import com.seiko.common.eventbus.EventBusActivityScope
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : FragmentActivity() {

    /**
     * 因为焦点问题，使用官方的Navigation来管理Fragment。
     * PS: 需要注意，Navigation在Fragment退栈时，会重新运行：
     *     onCreateView -> onViewCreated -> onActivityCreated -> onStart -> onResume
     *     尽量使用ViewModel存放数据。
     */
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = this.findNavController(R.id.myNavHostFragment)
    }

}