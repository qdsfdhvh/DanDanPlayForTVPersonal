plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdkVersion(Build.compileSdk)
    defaultConfig {
        minSdkVersion(Build.minSdk)
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Support
    api(Deps.multidex)
    api(Deps.coreKtx)
    api(Deps.appCompat)
    api(Deps.constraintLayout)
    api(Deps.preference)
    api(Deps.preferencex)
    api(Deps.lifecycle)
    api(Deps.lifecycleKtx)
    api(Deps.pagingKtx)
    api(Deps.leanback)
    api(Deps.leanbackPreference)
    api(Deps.fragment)
    api(Deps.fragmentKtx)

    // DataBase
    api(Deps.room_ktx)

    // Work
    api(Deps.work_runtimeKtx)

    // Kotlin
    api(Deps.kotlin_stdlib)
    api(Deps.kotlin_coroutines)

    // Network
    api(Deps.network_retrofit)
    api(Deps.network_retrofit_moshi)
    api(Deps.network_okhttp)
    api(Deps.network_okhttp_logging)

    // Json
    api(Deps.moshi_core)
    api(Deps.moshi_adapters)

    // 路由
    implementation("com.chenenyu.router:router:${Versions.router}")

    // Log
    api("com.jakewharton.timber:timber:4.7.1")

    // Toast
    implementation("com.github.Dovar66:DToast:1.1.6")

    // 屏幕自适应
    implementation("com.seiko.autosize:autosize:0.0.1")

    // Prefs
    implementation(Deps.mmkv_runtime)

    // Dagger
    api(Deps.daggerHiltAndroid)
    api(Deps.hiltCommon)
    api(Deps.hiltViewModel)
    kapt(Deps.daggerHiltCompiler)
    kapt(Deps.hiltCompiler)

    // viewBinding
    api("com.kirich1409.viewbindingpropertydelegate:viewbindingpropertydelegate:1.0.0")

    // 初始化 https://github.com/NoEndToLF/AppStartFaster
    api("com.github.NoEndToLF:AppStartFaster:1.0.3")

    // 图片加载库 - Glide https://github.com/bumptech/glide
    val glideVersion = "4.11.0"
    implementation("com.github.bumptech.glide:glide:${glideVersion}")
    kapt("com.github.bumptech.glide:compiler:${glideVersion}")

}
