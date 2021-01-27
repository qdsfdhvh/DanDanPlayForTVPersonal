plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
}

android {
    compileSdkVersion(Build.compileSdk)
    defaultConfig {
        minSdkVersion(Build.minSdk)
    }
}

dependencies {
    implementation(Deps.leanback)
    implementation(Deps.appCompat)
    implementation(Deps.lifecycle)
    implementation(Deps.viewModel)
}
