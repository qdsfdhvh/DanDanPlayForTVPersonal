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

    // 初始化
    api(Deps.startup)

    // Kotlin
    api(Deps.kotlin_stdlib)
    api(Deps.kotlin_coroutines)

    // Prefs
    api(Deps.mmkv_runtime)

    // Network
    api(Deps.network_retrofit)
    api(Deps.network_retrofit_moshi)
    api(Deps.network_okhttp)
    api(Deps.network_okhttp_logging)

    // Json
    api(Deps.moshi_core)
    api(Deps.moshi_adapters)

    // 路由
    api(Deps.arouter_api)

    // EventBus
    api("org.greenrobot:eventbus:3.1.1")

    // Log
    api("com.jakewharton.timber:timber:4.7.1")

    // Toast
    implementation("com.github.Dovar66:DToast:1.1.6")

    // 屏幕自适应
    implementation("com.seiko.autosize:autosize:0.0.1")

    // 图片
    implementation(Deps.glide)
    implementation(Deps.glideOkhttp)
    kapt(Deps.glideCompiler)

    // Dagger
    api(Deps.daggerHiltAndroid)
    api(Deps.hiltCommon)
    api(Deps.hiltViewModel)
    kapt(Deps.daggerHiltCompiler)
    kapt(Deps.hiltCompiler)

    // viewBinding
    api("com.kirich1409.viewbindingpropertydelegate:viewbindingpropertydelegate:1.0.0")
}
