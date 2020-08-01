plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(Build.compileSdk)
    defaultConfig {
        minSdkVersion(Build.minSdk)
    }
}

dependencies {
    api(project(":libs:tool-danmaku"))
}