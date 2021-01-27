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

        // 路由 https://github.com/chenenyu/Router
        classpath(Deps.routerPlugin)

        classpath("com.smallsoho.mobcase:McImage:1.5.1")
        classpath("com.mogujie.gradle:tinyPicPlugin:1.1.4")
        classpath("com.tencent.mm:AndResGuard-gradle-plugin:1.2.20")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven(url = "https://jitpack.io")
    }
}