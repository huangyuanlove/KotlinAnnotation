package com.huangyuanlove.processor

import com.huangyuanlove.annotations.IntentValue
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
        val builder = FunSpec.constructorBuilder().addParameter("target", className)
            .addParameter("view", viewClass)
            .addParameter("bundle", bundleClass)

        if (layoutIdStr != "") {
            builder.addStatement("target.setContentView(R.layout.%N)", layoutIdStr)
        }

        if (!intentValueMap.isNullOrEmpty()) {
            builder.addStatement("bundle?.let{")
            intentValueMap.forEach {
                addParseIntentType(it.value, builder)
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
        element: VariableElement,
        builder: FunSpec.Builder
    ) {
        val intentValue: IntentValue = element.getAnnotation(IntentValue::class.java)
        val keys = intentValue.key
        val targetKey = element.simpleName.toString()
        for (key in keys) {


            builder.beginControlFlow("if (it.containsKey(%S))", key)

            intentValue.key
            when (intentValue.type) {
                IntentValue.DEFAULT_TYPE -> {
                    when (element.asType().kind) {
                        TypeKind.BOOLEAN ->
                            builder.addStatement("target.%N = it.getBoolean(%S)", targetKey, key)
                        TypeKind.SHORT ->
                            builder.addStatement("target.%N = it.getShort(%S)", targetKey, key)
                        TypeKind.BYTE ->
                            builder.addStatement("target.%N = it.getByte(%S)", targetKey, key)
                        TypeKind.INT ->
                            builder.addStatement("target.%N = it.getInt(%S)", targetKey, key)
                        TypeKind.CHAR ->
                            builder.addStatement("target.%N = it.getChar(%S)", targetKey, key)
                        TypeKind.LONG ->
                            builder.addStatement("target.%N = it.getLong(%S)", targetKey, key)
                        TypeKind.FLOAT ->
                            builder.addStatement("target.%N = it.getFloat(%S)", targetKey, key)
                        TypeKind.DOUBLE ->
                            builder.addStatement("target.%N = it.getDouble(%S)", targetKey, key)

                        TypeKind.ARRAY -> {

                            when (element.asType().toString()) {
                                "byte[]" ->
                                    builder.addStatement("target.%N = it.getByteArray(%S)", targetKey, key)
                                "int[]" ->
                                    builder.addStatement("target.%N = it.getIntArray(%S)", targetKey, key)
                                "char[]" ->
                                    builder.addStatement("target.%N = it.getCharArray(%S)", targetKey, key)
                                "float[]" ->
                                    builder.addStatement("target.%N = it.getFloatArray(%S)", targetKey, key)
                                "java.lang.CharSequence[]" ->
                                    builder.addStatement(
                                        "target.%N = it.getCharSequenceArray(%S)",
                                        targetKey,
                                        key
                                    )
                                "java.lang.String[]" ->
                                    builder.addStatement(
                                        "target.%N = it.getStringArray(%S)",
                                        targetKey,
                                        key
                                    )
                                "boolean[]" ->
                                    builder.addStatement(
                                        "target.%N = it.getBooleanArray(%S)",
                                        targetKey,
                                        key
                                    )
                                "short[]" ->
                                    builder.addStatement(
                                        "target.%N = it.getShortArray(%S)",
                                        targetKey,
                                        key
                                    )
                                "double[]" ->
                                    builder.addStatement(
                                        "target.%N = it.getDoubleArray(%S)",
                                        targetKey,
                                        key
                                    )
                                "long[]" ->
                                    builder.addStatement(
                                        "target.%N = it.getLongArray(%S)",
                                        targetKey,
                                        key
                                    )


                                else -> {
                                    builder.addStatement(
                                        "target.%N = it.getParcelableArray(%S) as %T",
                                        targetKey,
                                        key,
                                        element.asType()
                                    )
                                }
                            }
                        }


                        TypeKind.DECLARED -> {
                            when (element.asType().toString()) {
                                "java.lang.String" ->
                                    builder.addStatement(
                                        "target.%N = it.getString(%S,\"\")",
                                        targetKey,
                                        key
                                    )
                                "java.util.ArrayList<java.lang.String>"->
                                    builder.addStatement(
                                        "target.%N = it.getStringArrayList(%S)",
                                        targetKey,
                                        key
                                    )
                                "java.lang.CharSequence"->
                                    builder.addStatement(
                                        "target.%N = it.getCharSequence(%S)",
                                        targetKey,
                                        key
                                    )
                                "java.util.ArrayList<java.lang.Integer>"->
                                    builder.addStatement(
                                        "target.%N = it.getIntegerArrayList(%S)",
                                        targetKey,
                                        key
                                    )
                                "java.util.ArrayList<java.lang.CharSequence>"->
                                    builder.addStatement(
                                        "target.%N = it.getCharSequenceArrayList(%S)",
                                        targetKey,
                                        key
                                    )


                                else ->
                                    log("TypeKind.DECLARED: ${key}  ${element.asType().toString()}")
                            }
                        }
                        else ->
                            log("没有找到对应的的类型 ${key}  ${element.asType().toString()}")

                    }
                }
                IntentValue.SERIALIZABLE_OBJECT -> {
                    builder.addStatement(
                        "target.%N = it.getSerializable(%S) as %T ",
                        targetKey,
                        key,
                        element.asType()
                    )
                }
                IntentValue.PARCELABLE_OBJECT -> {
                    builder.addStatement(
                        "target.%N = it.getParcelable<%T>(%S)",
                        targetKey,
                        element.asType(),
                        key

                    )
                }
            }
            builder.endControlFlow()
        }
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