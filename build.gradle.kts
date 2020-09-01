buildscript {
    repositories {
        google()
        jcenter()
        maven(url = "https://jitpack.io")
    }
    dependencies {
        classpath(Deps.gradle)
        classpath(Deps.kotlinPlugin)
        classpath(Deps.ccRegister)
        classpath(Deps.daggerPlugin)
        classpath(Deps.navigationSafeArgs)

        classpath("com.smallsoho.mobcase:McImage:1.5.0")
        classpath("com.mogujie.gradle:tinyPicPlugin:1.1.4")
        classpath("com.tencent.mm:AndResGuard-gradle-plugin:1.2.18")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven(url = "https://jitpack.io")
    }
}