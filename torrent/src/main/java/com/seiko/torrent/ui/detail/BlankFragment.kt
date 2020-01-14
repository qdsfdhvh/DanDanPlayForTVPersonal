package com.seiko.torrent.ui.detail

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.LogUtils
import com.seiko.torrent.R
import com.seiko.torrent.ui.base.BaseFragment
import com.seiko.torrent.vm.MainViewModel
import kotlinx.android.synthetic.main.torrent_fragment_blank.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel

class BlankFragment : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.torrent_fragment_blank
    }


}