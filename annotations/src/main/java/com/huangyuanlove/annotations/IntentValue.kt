package com.huangyuanlove.annotations

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class IntentValue(vararg val key: String, val type: Int = DEFAULT_TYPE) {
    companion object {
        const val DEFAULT_TYPE = -1
        const val PARCELABLE_OBJECT = 1
        const val SERIALIZABLE_OBJECT = 2
    }
}

