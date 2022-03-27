package com.huangyuanlove.annotations

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class SchemeArgs(vararg val key: String)
