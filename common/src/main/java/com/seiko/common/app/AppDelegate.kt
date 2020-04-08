package com.seiko.common.app

import android.app.Application
import android.content.res.Configuration

interface AppDelegate {
    fun Application.setupApplication()
    fun Application.clearOnLowMemory()
    fun Application.configurationChanged(newConfig: Configuration)
}

class AppSetupDelegate : AppDelegate {

    private val map = HashMap<String, InitComponent>()

    override fun Application.setupApplication() {
        initComponents()
       for (entry in map) {
           entry.value.onCreate(this)
       }
    }

    override fun Application.clearOnLowMemory() {
        for (entry in map) {
            entry.value.onLowMemory()
        }
    }

    override fun Application.configurationChanged(newConfig: Configuration) {
        for (entry in map) {
            entry.value.onConfigurationChanged(newConfig)
        }
    }

    /**
     * 基于app-cc-setting.gradle中的配置：
     *   ccregister.registerInfo.add([
     *     //在自动注册组件的基础上增加：自动注册组件B的processor
     *     'scanInterface'             : 'org.seiko.common.app.InitComponent'
     *     , 'codeInsertToClassName'   : 'org.seiko.common.app.AppSetupDelegate'
     *     , 'codeInsertToMethodName'  : 'initComponents'
     *     , 'registerMethodName'      : 'add'
     *   ])
     * InitComponent的所有实现类会在编译时自动实现add放啊，并添加到initComponents中，比如：
     *   private fun initComponents() {
     *       add(Simple1InitComponents())
     *       add(Simple2InitComponents())
     *   }
     */
    private fun initComponents() {

    }

    private fun add(component: InitComponent) {
        map[component.javaClass.name] = component
    }

}