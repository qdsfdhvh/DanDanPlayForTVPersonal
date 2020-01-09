package com.seiko.module.torrent.di

import com.seiko.data.constants.TORRENT_DOWNLOAD_DIR
import com.seiko.module.torrent.vm.AddTorrentViewModel
import com.seiko.module.torrent.vm.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val viewModelModule = module {

    viewModel {
        MainViewModel(get())
    }

    viewModel {
        AddTorrentViewModel(get(), get(named(TORRENT_DOWNLOAD_DIR)),
            get(), get())
    }

}