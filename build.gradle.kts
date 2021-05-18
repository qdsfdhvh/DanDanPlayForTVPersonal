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
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven(url = "https://jitpack.io")
    }
}