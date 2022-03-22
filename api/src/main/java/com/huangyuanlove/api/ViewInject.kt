package com.huangyuanlove.api

import android.app.Activity
import android.view.View
import java.lang.reflect.InvocationTargetException

object ViewInject {

    //类似ButterKnife方法
    fun bind(target: Activity) {
        println("ViewInject # bind ")
        val sourceView = target.window.decorView
        createBinding(target, sourceView)
    }

    private fun createBinding(target: Activity, source: View) {
        val targetClass = target::class.java
        var className = targetClass.name
        try {
            //获取类名
            val bindingClass = targetClass.classLoader!!.loadClass(className + "_BindTest")
            //获取构造方法
            val constructor = bindingClass.getConstructor(targetClass, View::class.java)
            //向方法中传入数据activity和view
            constructor.newInstance(target, source)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }
}