package com.huangyuanlove.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.element.*

//存储一个Activity文件所有注解数据，并有相应方法生成编译后的文件
class InjectInfo(val element: TypeElement) {


    //类名
    val className: ClassName = element.asClassName()
    val viewClass: ClassName = ClassName("android.view", "View")

    //包名
    val packageName: String = getPackageName(element).qualifiedName.toString()

    //布局只有一个id
    var layoutId: String = ""

    //View 注解数据可能有多个 注意是VariableElement
    val viewMap = hashMapOf<String, VariableElement>()

    //点击事件 注解数据可能有多个 注意是ExecutableElement
    val clickListenerMap = hashMapOf<String, ExecutableElement>()

    private fun getPackageName(element: Element): PackageElement {
        var e = element
        while (e.kind != ElementKind.PACKAGE) {
            e = e.enclosingElement
        }
        return e as PackageElement
    }

    fun getClassName(element: Element): ClassName {
        var elementType = element.asType().asTypeName()

        return elementType as ClassName
    }

    //自动生成构造方法，主要使用kotlinpoet
    fun generateConstructor(): FunSpec {        //构造方法，传入activity参数
        val builder = FunSpec.constructorBuilder().addParameter("target", className)
            .addParameter("view", viewClass)

        if (layoutId != "") {
            builder.addStatement(" var resourceID = view.getResources().getIdentifier(%S,%S, view.getContext().getPackageName())", layoutId, "id");

            builder.addStatement("target.setContentView(resourceID)")
        }

        viewMap.forEach { (id, variableElement) ->
            builder.addStatement(
                "target.%N = view.findViewById(%L)",
                variableElement.simpleName,
                id
            )
        }

        clickListenerMap.forEach { (id, element) ->

            when (element.parameters.size) {
                //没有参数
                0 -> builder.addStatement(
                    "(view.findViewById(%L) as View).setOnClickListener{target.%N()}", id
                )
                //一个参数
                1 -> {
                    if (getClassName(element.parameters[0]) != viewClass) {
                        println("------------")
                        println("element.simpleName function parameter error")
                        println("============")

                    }
                    builder.addStatement(
                        "(view.findViewById(%L) as View).setOnClickListener{target.%N(it)}",
                        id,
                        element.simpleName
                    )
                }
                //多个参数错误
                else -> {
                    println("------------")
                    println("element.simpleName function parameter error")
                    println("============")
                }
            }

        }

        return builder.build()
    }

}