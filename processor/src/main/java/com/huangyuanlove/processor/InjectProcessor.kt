package com.huangyuanlove.processor

import com.google.auto.service.AutoService
import com.huangyuanlove.annotations.BindView
import com.huangyuanlove.annotations.ClickResponder
import com.huangyuanlove.annotations.ContentView
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.tools.Diagnostic


@AutoService(Processor::class)
class InjectProcessor : AbstractProcessor() {
    companion object {
        private const val PICK_END = "_BindTest"
    }
    private lateinit var message: Messager

    //存储类文件数据
    private val mInjectMaps = hashMapOf<String, InjectInfo>()
    //必须实现方法
    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment
    ): Boolean {
        //里面就要生成我们需要的文件

        log("--------------------")
        log("process start")
        log("====================")

        roundEnv.getElementsAnnotatedWith(BindView::class.java).forEach {

        }


        return true
    }
    //把注解类都添加进行，这个方法一看方法名就应该知道干啥的
    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(
            ContentView::class.java.canonicalName,
            BindView::class.java.canonicalName,
            ClickResponder::class.java.canonicalName
        )
    }

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        message = processingEnv.messager
    }

    fun log(msg:String){
        message.printMessage(Diagnostic.Kind.WARNING,msg)
    }
}