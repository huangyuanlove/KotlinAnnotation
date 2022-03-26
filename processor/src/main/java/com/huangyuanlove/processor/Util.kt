package com.huangyuanlove.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.element.Element
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName

/**
 * 获取需要把java类型映射成kotlin类型的ClassName  如：java.lang.String 在kotlin中的类型为kotlin.String 如果是空则表示该类型无需进行映射
 */

public fun javaToKotlinType(typeName: String): ClassName? {
    //由于java中的装拆箱机制，所以会出现int等类型无法识别的问题，javatoKotlin可以识别装箱后的类型，无法识别int,short,double这些类型，所以手动做一个装箱
    var name = javaPacking(typeName)
    val className = JavaToKotlinClassMap.INSTANCE.mapJavaToKotlin(FqName(name))?.asSingleFqName()?.asString()
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