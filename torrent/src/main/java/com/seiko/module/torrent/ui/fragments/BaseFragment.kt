package com.seiko.module.torrent.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.LogUtils

abstract class BaseFragment : Fragment() {

    @LayoutRes abstract fun getLayoutId(): Int

//    override fun onAttach(context: Context) {
//        LogUtils.d("${javaClass.simpleName} - onAttach(${context.javaClass.simpleName})")
//        super.onAttach(context)
//    }
//
//    override fun onAttachFragment(childFragment: Fragment) {
//        LogUtils.d("${javaClass.simpleName} - onAttachFragment(${childFragment.javaClass.simpleName})")
//        super.onAttachFragment(childFragment)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        LogUtils.d("${javaClass.simpleName} - onCreate")
//        super.onCreate(savedInstanceState)
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        LogUtils.d("${javaClass.simpleName} - onCreateView")
        return inflater.inflate(getLayoutId(), container, false)
    }

//    override fun onStart() {
//        LogUtils.d("${javaClass.simpleName} - onStart")
//        super.onStart()
//    }
//
//    override fun onResume() {
//        LogUtils.d("${javaClass.simpleName} - onResume")
//        super.onResume()
//    }
//
//    override fun onPause() {
//        LogUtils.d("${javaClass.simpleName} - onPause")
//        super.onPause()
//    }
//
//    override fun onStop() {
//        LogUtils.d("${javaClass.simpleName} - onStop")
//        super.onStop()
//    }
//
//    override fun onDestroyView() {
//        LogUtils.d("${javaClass.simpleName} - onDestroyView")
//        super.onDestroyView()
//    }
//
//    override fun onDestroy() {
//        LogUtils.d("${javaClass.simpleName} - onDestroy")
//        super.onDestroy()
//    }
//
//    override fun onDetach() {
//        LogUtils.d("${javaClass.simpleName} - onDetach")
//        super.onDetach()
//    }

}