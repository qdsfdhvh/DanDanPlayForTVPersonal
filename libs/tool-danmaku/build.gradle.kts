plugins {
    id("com.android.library")
}

android {
    compileSdkVersion(Build.compileSdk)
    defaultConfig {
        minSdkVersion(Build.minSdk)
    }
}

dependencies {
    implementation("com.github.ctiao:ndkbitmap-armv5:0.9.21")
    implementation("com.github.ctiao:ndkbitmap-armv7a:0.9.21")
    implementation("com.github.ctiao:ndkbitmap-x86:0.9.21")
}