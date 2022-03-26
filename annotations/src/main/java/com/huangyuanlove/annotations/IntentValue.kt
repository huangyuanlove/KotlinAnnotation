package com.huangyuanlove.annotations

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class IntentValue(vararg val key:String)
