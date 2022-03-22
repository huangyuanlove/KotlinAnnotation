package com.huangyuanlove.kotlinannotation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.huangyuanlove.annotations.BindView
import com.huangyuanlove.annotations.ClickResponder
import com.huangyuanlove.annotations.ContentView
import com.huangyuanlove.api.ViewInject

@ContentView(idStr = "activity_main")
class MainActivity : AppCompatActivity() {

    @BindView(idStr = "view_bind_test")
    lateinit var view_bind_test: TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        ViewInject.bind(this)
    }

//    @ClickResponder(idStr = {"click_responder_test"})
//    fun onClickResponderTest( view:View){
//        Toast.makeText(this,"click",Toast.LENGTH_LONG).show()
//    }
}