# 弹弹Play For TV Personal

> 通过弹弹Play的开源API所开发的android tv端个人用app。

## 架构相关

CleanArchitecture
Clean思想由Uncle Bob提出的，相关内容详见[The Clean Code Blog](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)。  

架构参考DEMO:
[Android-CleanArchitecture-Kotlin](https://github.com/android10/Android-CleanArchitecture-Kotlin)、
[Plaid](https://github.com/android/plaid)、
[KotlinAndroid](https://github.com/guofudong/KotlinAndroid)、
[Notre-Dame](https://github.com/ApplETS/Notre-Dame)

视频观看:
[Bilibili - Kotlin 语言帮助开发者更好的构建应用](https://www.bilibili.com/video/av70762038)

## 项目截图

![SCREEN](./screen/device-2019-10-28-193951.png)

## 第三方库

### 参考

01) [DanDanPlayForAndroid](https://github.com/xyoye/DanDanPlayForAndroid)
弹弹play 概念版，弹弹play系列应用安卓平台上的实现

### 1.网络请求

01) [okhttp](https://github.com/square/okhttp)
安卓端最火热的轻量级HTTP网络请求框架

02) [retrofit](https://github.com/square/retrofi)
一个Restful的HTTP网络请求框架的封装

### 2.工具

01) [Koin](https://github.com/InsertKoinIO/koin)
一款轻量级的依赖注入框架

02) [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines)
Kotlin协程

03) [Fresco](https://github.com/facebook/fresco)
Facebook开源的一个强大的图片加载组件

04) [MMKV](https://github.com/Tencent/MMKV)
基于 mmap 内存映射的 key-value 组件

05) [EventBus](https://github.com/greenrobot/EventBus)
一款在 Android 开发中使用的发布/订阅事件总线框架

06) [AndroidAutoSize](https://github.com/JessYanCoding/AndroidAutoSize)
一个极低成本的 Android 屏幕适配方案

07) [AndroidUtilCode](https://github.com/Blankj/AndroidUtilCode)
一个强大易用的安卓工具类库

08) [Eazy Runtime Permission](https://github.com/sagar-viradiya/eazypermissions)
一个轻量的Android权限库

09) [Navigator](https://github.com/florent37/Navigator)
Android Multi-module navigator, trying to find a way to navigate into a modularized android project

10) [okio](https://github.com/square/okio)
一个非常优秀的Java IO/NIO封装库

### 3.播放器相关

01) [Exoplayer](https://github.com/google/ExoPlayer)
Android系统的应用级媒体播放器

02) [ijkplayer](https://github.com/bilibili/ijkplayer)
Bilibili开源媒体播放器

03) [DanmakuFlameMaster](https://github.com/bilibili/DanmakuFlameMaster)
Bilibili开源弹幕引擎

04) [juniversalchardet](https://github.com/albfernandez/juniversalchardet)
识别字幕文件编码格式

### 4.Torrent相关

01) [jlibtorrent](https://github.com/frostwire/frostwire-jlibtorrent)
种子下载

02) [simple-torrent-android](https://github.com/masterwok/simple-torrent-android)
基于jlibtorrent二次封装的kotlin库

03) [libretorrent](https://github.com/proninyaroslav/libretorrent)
基于jlibtorrent开发的开源BT下载应用