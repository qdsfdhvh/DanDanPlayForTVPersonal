package com.dandanplay.tv.ui

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.dandanplay.tv.R
import com.dandanplay.tv.ui.base.BaseFragment
import kotlinx.coroutines.*

class SplashFragment: BaseFragment() {

    private var job: Job? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        job = GlobalScope.launch(Dispatchers.Main) {
            delay(400)
            launchMain()
        }
    }

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }

    private fun launchMain() {
        findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())

    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_splash
    }

}