package com.huangyuanlove.annotations



@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class BindView(val idStr:String)
