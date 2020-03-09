buildscript {
    repositories {
        google()
        jcenter()
        maven(url = "https://jitpack.io")
    }
    dependencies {
        classpath(Deps.plugin_gradle)
        classpath(Deps.plugin_kotlin)
        classpath(Deps.plugin_cc)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven(url = "https://jitpack.io")
        maven(url = "https://dl.bintray.com/videolan/Android")
    }
}