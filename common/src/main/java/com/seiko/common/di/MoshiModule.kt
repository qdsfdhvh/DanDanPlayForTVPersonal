package com.seiko.common.di

import com.squareup.moshi.Moshi
import org.koin.dsl.module

val moshiModule = module {
    single { createMoshi() }
}

private fun createMoshi(): Moshi {
    return Moshi.Builder().build()
}