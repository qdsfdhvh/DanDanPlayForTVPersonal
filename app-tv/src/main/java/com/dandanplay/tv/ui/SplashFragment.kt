package com.dandanplay.tv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dandanplay.tv.R
import com.dandanplay.tv.ui.SplashFragmentDirections
import kotlinx.coroutines.*

class SplashFragment: Fragment() {

    private var job: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

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

}