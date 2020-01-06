package com.seiko.module.torrent.di

import com.seiko.data.constants.TORRENT_DOWNOLAD_DIR
import com.seiko.module.torrent.vm.AddTorrentViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val viewModelModule = module {

    viewModel {
        AddTorrentViewModel(get(), get(named(TORRENT_DOWNOLAD_DIR)),
            get(), get())
    }

}