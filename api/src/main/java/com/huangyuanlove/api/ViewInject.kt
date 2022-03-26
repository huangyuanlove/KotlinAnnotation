package com.huangyuanlove.api

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import java.lang.reflect.InvocationTargetException

object ViewInject {

    //类似ButterKnife方法
    fun bind(target: Activity) {
        println("ViewInject # bind Activity")
        val sourceView = target.window.decorView
        createBinding(target, sourceView,target.intent.extras)
    }
    fun bind(target: Fragment,view: View) {
        println("ViewInject # bind Fragment")
        createBinding(target, view,target.arguments)
    }


    private fun createParseBundle(target:Any,bundle:Bundle){

    }

    private fun createBinding(target: Any, source: View,bundle: Bundle?) {
        val targetClass = target::class.java
        var className = targetClass.name
        try {
            //获取类名
            val bindingClass = targetClass.classLoader!!.loadClass(className + "_BindTest")
            //获取构造方法
            val constructor = bindingClass.getConstructor(targetClass, View::class.java,Bundle::class.java)
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