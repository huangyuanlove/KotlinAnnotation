package com.huangyuanlove.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import javax.annotation.processing.Messager
import javax.lang.model.element.*
import javax.lang.model.type.TypeKind
import javax.tools.Diagnostic
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName


/**
 * 每个类对应一个FileSpecWrapper
 */
class FileSpecWrapper(private val element: TypeElement, val messager: Messager) {

    //类名
    val className: ClassName = element.asClassName()
    val viewClass: ClassName = ClassName("android.view", "View")
    val bundleClass: ClassName = ClassName("android.os", "Bundle")

    //包名
    val packageName: String = getPackageName(element).qualifiedName.toString()

    //布局只有一个id
    var layoutIdStr: String = ""

    //View 注解数据可能有多个 注意是VariableElement
    val viewMap = hashMapOf<String, VariableElement>()

    //点击事件 注解数据可能有多个 注意是ExecutableElement
    val clickListenerMap = hashMapOf<String, ExecutableElement>()

    //从Intent中取值
    val intentValueMap = hashMapOf<String, VariableElement>()


    private fun getPackageName(element: Element): PackageElement {
        var e = element
        while (e.kind != ElementKind.PACKAGE) {
            e = e.enclosingElement
        }
        return e as PackageElement
    }

    private fun getClassName(element: Element): ClassName {
        var elementType = element.asType().asTypeName()

        return elementType as ClassName
    }


    fun generateConstructor(): FunSpec {
        log("generateConstructor")
        val builder = FunSpec.constructorBuilder().addParameter("target", className)
            .addParameter("view", viewClass)
            .addParameter("bundle", bundleClass)

        if (layoutIdStr != "") {
            log("layoutIdStr not null:${layoutIdStr}")
            builder.addStatement("target.setContentView(R.layout.%N)", layoutIdStr)
        }

        if (!intentValueMap.isNullOrEmpty()) {


            builder.addStatement("bundle?.let{")
            intentValueMap.forEach {
                log("${it.key} ->${it.value.asType().kind}")
                addParseIntentType(it.key, it.value, builder)
            }
            builder.addStatement("}")
        }


        viewMap.forEach { (idStr, variableElement) ->

            builder.addStatement(
                "target.%N = view.findViewById(R.id.%N)",
                variableElement.simpleName, idStr

            )
        }

        clickListenerMap.forEach { (idStr, element) ->
            log("$idStr :-> ${element}")

            when (element.parameters.size) {
                //没有参数
                0 -> builder.addStatement(
                    "(view.findViewById(R.id.%N) as View).setOnClickListener{target.%N()}", idStr
                )
                //一个参数
                1 -> {
                    if (getClassName(element.parameters[0]) != viewClass) {
                        log("element.simpleName function parameter error")
                    }
                    builder.addStatement(
                        "(view.findViewById(R.id.%N) as View).setOnClickListener{target.%N(it)}",
                        idStr,
                        element.simpleName
                    )
                }
                //多个参数错误
                else -> log("element.simpleName function parameter error")
            }

        }

        return builder.build()
    }

    private fun addParseIntentType(
        key: String,
        element: VariableElement,
        builder: FunSpec.Builder
    ) {
        builder.beginControlFlow("if (it.containsKey(%S))", key)
        when (element.asType().kind) {
            TypeKind.BOOLEAN,TypeKind.BYTE,TypeKind.SHORT,TypeKind.INT, TypeKind.LONG,TypeKind.CHAR,TypeKind.FLOAT,TypeKind.DOUBLE,->


                builder.addStatement("target.%N = it.get(%S) as %T", key, key, element.asType())


        }
        builder.endControlFlow()
    }

    private fun log(msg: String) {
        messager.printMessage(Diagnostic.Kind.OTHER, "log--> $msg")
    }

    public fun javaToKotlinType(typeName: String): ClassName? {
        //由于java中的装拆箱机制，所以会出现int等类型无法识别的问题，javatoKotlin可以识别装箱后的类型，无法识别int,short,double这些类型，所以手动做一个装箱
        var name = javaPacking(typeName)
        val className =
            JavaToKotlinClassMap.INSTANCE.mapJavaToKotlin(FqName(name))?.asSingleFqName()
                ?.asString()
        return if (className == null) {
            null
        } else {
            ClassName.bestGuess(className)
        }
    }

    /**
     * 对java的基本类型进行装箱操作
     */
    private fun javaPacking(typeName: String): String {
        return when (typeName) {
            "int" -> "java.lang.Integer"
            "byte" -> "java.lang.Byte"
            "short" -> "java.lang.Short"
            "long" -> "java.lang.Long"
            "double" -> "java.lang.Double"
            "float" -> "java.lang.Float"
            "boolean" -> "java.lang.Boolean"
            "char" -> "java.lang.Character"
            else -> typeName
        }
    }

}