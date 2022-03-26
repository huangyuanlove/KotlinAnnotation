package com.huangyuanlove.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class ClickResponder(vararg val idStr:String)
