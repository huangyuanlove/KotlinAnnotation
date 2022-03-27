package com.huangyuanlove.kotlinannotation.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class DataP(val name:String,val age:Int) :Parcelable{

}