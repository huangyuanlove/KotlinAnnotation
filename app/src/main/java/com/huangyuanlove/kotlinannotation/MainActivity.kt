package com.huangyuanlove.kotlinannotation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.huangyuanlove.annotations.*
import com.huangyuanlove.api.ViewInject
import com.huangyuanlove.kotlinannotation.data.DataP
import com.huangyuanlove.kotlinannotation.data.DataS

@ContentView("activity_main")
class MainActivity : AppCompatActivity() {

    @BindView("view_bind_test")
    lateinit var view_bind_test: TextView


    @IntentValue("aByte","bByte","cByte","dByte")
    var aByte: Byte = 1

    @IntentValue("aBooleanA")
    var aBoolean: Boolean = false

    @IntentValue("aChar")
    var aChar: Char = 'a'

    @IntentValue("aShort")
    var aShort: Short = 2

    @IntentValue("aFloat")
    var aFloat: Float = 0.0f

    @SchemeArgs("aString")
    @IntentValue("aString")
    var aString : String = "abc"

    @IntentValue("aInt")
    var aInt: Int = 0

    @IntentValue("aLong")
    var aLong: Long = 0L;

    @IntentValue("aDouble")
    var aDouble: Double = 0.0

    @IntentValue("DataS", type = IntentValue.SERIALIZABLE_OBJECT)
    var  dataS :DataS? =null

    @IntentValue("DataArray")
    var  dataArray :Array<DataS> = arrayOf()

    @IntentValue("byteArray")
    var  byteArray :ByteArray? =null

    @IntentValue("aShortArray")
    var shortArray :ShortArray?=null
    @IntentValue("aBooleanArray")
    var booleanArray : BooleanArray? = null

    @IntentValue("aIntArray")
    var intArray : IntArray? = null

    @IntentValue("aLongArray")
    var longArray : LongArray? = null

    @IntentValue("aCharArray")
    var charArray : CharArray? = null

    @IntentValue("aCharSequenceArray")
    var charSequenceArray : Array<CharSequence> ? = null

    @IntentValue("aFloatArray")
    var floatArray : FloatArray ?=null

    @IntentValue("aDoubleArray")
    var doubleArray :DoubleArray?=null

    @IntentValue("aStringArray")
    var stringArray : Array<String>? = null

    @IntentValue("aIntegerArrayList")
    var integerArrayList : ArrayList<Int>? = null

    @IntentValue("aCharSequenceArrayList")
    var charSequenceArrayList : ArrayList<CharSequence>? = null

    @IntentValue("aStringArrayList")
    var stringArrayList : ArrayList<String>? = null
    @IntentValue("aCharSequence")
    var charSequence : CharSequence? = null

    @IntentValue("aDataP", type = IntentValue.PARCELABLE_OBJECT)
    var dataP:DataP? = null

    @SchemeArgs("aUriBoolean")
    var uriBoolean :Boolean = true

    @SchemeArgs("aUriInt")
    var uriInt :Int = -1

    @SchemeArgs("aUriString")
    var uriString :String = "a"

    @SchemeArgs("aUriDouble")
    var uriDouble :Double = 1.1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ViewInject.bind(this)
        Toast.makeText(this,"aString->${aString}",Toast.LENGTH_SHORT).show()


    }

    @ClickResponder("click_responder_test", "click_responder_test2")
    fun onClickResponderTest(view: View) {
        Toast.makeText(this, "click", Toast.LENGTH_LONG).show()
    }
}