package com.huangyuanlove.annotations
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class ClickResponder(val id:IntArray, val idStr:Array<String>)
