package com.huangyuanlove.kotlinannotation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.huangyuanlove.annotations.BindView
import com.huangyuanlove.annotations.ClickResponder
import com.huangyuanlove.annotations.ContentView
import com.huangyuanlove.annotations.IntentValue
import com.huangyuanlove.api.ViewInject

class InjectFragment : Fragment(){

    @BindView("test_view_inject")
    lateinit var test_view_inject: TextView

    @IntentValue("injectFragment")
    lateinit var injectFragment:String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View = inflater.inflate(R.layout.fragment_test_inject,null)
        ViewInject.bind(this,view)
        return view;
    }

    @ClickResponder("test_view_inject")
    fun onClickTestViewInject(view:View){
        Toast.makeText(context,"fragment点击控件",Toast.LENGTH_SHORT).show()
    }
}