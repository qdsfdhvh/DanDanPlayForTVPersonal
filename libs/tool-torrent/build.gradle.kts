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
    implementation(Deps.annotation)

    // Torrent
    val libtorrent4j = "1.2.3.0"
    api("org.libtorrent4j:libtorrent4j:${libtorrent4j}")
    implementation("org.libtorrent4j:libtorrent4j-android-arm:${libtorrent4j}")
    implementation("org.libtorrent4j:libtorrent4j-android-arm64:${libtorrent4j}")
    implementation("org.libtorrent4j:libtorrent4j-android-x86:${libtorrent4j}")
    implementation("org.libtorrent4j:libtorrent4j-android-x86_64:${libtorrent4j}")
}