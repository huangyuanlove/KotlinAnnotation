package com.huangyuanlove.kotlinannotation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.huangyuanlove.annotations.BindView
import com.huangyuanlove.annotations.ClickResponder
import com.huangyuanlove.annotations.ContentView
import com.huangyuanlove.annotations.IntentValue
import com.huangyuanlove.api.ViewInject
import com.huangyuanlove.kotlinannotation.data.DataS

@ContentView("activity_main")
class MainActivity : AppCompatActivity() {

    @BindView("view_bind_test")
    lateinit var view_bind_test: TextView


    @IntentValue("aByte")
    var aByte: Byte = 1

    @IntentValue("aBoolean")
    var aBoolean: Boolean = false

    @IntentValue("aChar")
    var aChar: Char = 'a'

    @IntentValue("aShort")
    var aShort: Short = 2

    @IntentValue("aFloat")
    var aFloat: Float = 0.0f

    @IntentValue("aString")
    var aString : String = "abc"



    @IntentValue("aInt")
    var aInt: Int = 0

    @IntentValue("aLong")
    var aLong: Long = 0L;

    @IntentValue("aDouble")
    var aDouble: Double = 0.0

    @IntentValue("DataS")
    val  dataS :DataS? =null

    @IntentValue("DataArray")
    val  dataArray :Array<DataS> = arrayOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ViewInject.bind(this)


    }

    @ClickResponder("click_responder_test", "click_responder_test2")
    fun onClickResponderTest(view: View) {
        Toast.makeText(this, "click", Toast.LENGTH_LONG).show()
    }
}