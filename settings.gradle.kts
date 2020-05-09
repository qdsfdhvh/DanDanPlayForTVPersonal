rootProject.name="DanDanPlayForTV"
include(":app",
        ":module-tv",
        ":module-torrent",
        ":module-player",
        ":common",

        ":libs:thunder",
        ":libs:file-picker",
        ":libs:tool-danmaku",
        ":libs:tool-danma",
        ":libs:tool-torrent")

include(":libs:tool-vlc",
        ":libs:tool-vlc:libvlc",
        ":libs:tool-vlc:extension-api",
        ":libs:tool-vlc:medialibrary",
//        ":libs:tool-vlc:application:app",
        ":libs:tool-vlc:application:live-plot-graph",
        ":libs:tool-vlc:application:mediadb",
        ":libs:tool-vlc:application:moviepedia",
        ":libs:tool-vlc:application:resources",
        ":libs:tool-vlc:application:television",
        ":libs:tool-vlc:application:tools",
        ":libs:tool-vlc:application:vlc-android")


val beginOfSetting = System.currentTimeMillis()
var beginOfConfig: Long = 0
var configHasBegin = false
val beginOfProjectConfig = HashMap<Project, Long>()
var beginOfProjectExcute: Long = 0

gradle.projectsLoaded {
        println("初始化阶段，耗时：${System.currentTimeMillis() - beginOfSetting}ms")
}

gradle.beforeProject {
        if (!configHasBegin) {
                configHasBegin = true
                beginOfConfig = System.currentTimeMillis()
        }
        beginOfProjectConfig[this] = System.currentTimeMillis()
}

gradle.afterProject {
        val begin = beginOfProjectConfig[project] ?: 0
        println("配置阶段，${this}耗时：${System.currentTimeMillis() - begin}ms")
}

gradle.taskGraph.whenReady {
        println("配置阶段，总共耗时：${System.currentTimeMillis() - beginOfConfig}ms")
        beginOfProjectExcute = System.currentTimeMillis()
}

gradle.taskGraph.beforeTask {
        doFirst {
                extensions.add("beginOfTask", System.currentTimeMillis())
        }
        doLast {
                println("执行阶段，${this}耗时：${System.currentTimeMillis() - (extensions["beginOfTask"] as Long)}ms")
        }
}

gradle.buildFinished {
        println("执行阶段，耗时：${System.currentTimeMillis() - beginOfProjectExcute}ms")
}

gradle.addBuildListener(object : BuildListener {
        override fun buildStarted(gradle: Gradle) {
                println("开始构建")
        }

        override fun settingsEvaluated(settings: Settings) {
                println("settings 评估完成（settings.gradle 中代码执行完毕）")
        }

        override fun projectsLoaded(gradle: Gradle) {
                println("项目结构加载完成（初始化阶段结束）")
                println("初始化结束，可访问根项目：${gradle.gradle.rootProject}")
        }

        override fun projectsEvaluated(gradle: Gradle) {
                println("所有项目评估完成（配置阶段结束）")
        }

        override fun buildFinished(result: BuildResult) {
                println("构建结束")
        }
})