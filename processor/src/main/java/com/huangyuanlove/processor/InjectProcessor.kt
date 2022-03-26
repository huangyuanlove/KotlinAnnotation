package com.huangyuanlove.processor

import com.google.auto.service.AutoService
import com.huangyuanlove.annotations.BindView
import com.huangyuanlove.annotations.ClickResponder
import com.huangyuanlove.annotations.ContentView
import com.huangyuanlove.annotations.IntentValue
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.element.*
import javax.lang.model.util.Elements
import javax.tools.Diagnostic


@AutoService(Processor::class)
class InjectProcessor : AbstractProcessor() {
    companion object {
        private const val PICK_END = "_BindTest"
    }
    private lateinit var messager: Messager


    //存储类文件数据
    private val mInjectMaps = hashMapOf<String, FileSpecWrapper>()

    //必须实现方法
    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment
    ): Boolean {
        collectInfo(roundEnv)

        mInjectMaps.forEach { (name, info) ->
            //这里生成文件
            val file= FileSpec.builder(info.packageName, info.className.simpleName + PICK_END)
                .addType(
                    TypeSpec.classBuilder(info.className.simpleName + PICK_END)
                        .primaryConstructor(info.generateConstructor()).build()
                ).build()

            file.writeFile()
        }

        return true
    }

    private fun collectInfo(roundEnv: RoundEnvironment){
        roundEnv.getElementsAnnotatedWith(ContentView::class.java).forEach {
            collectContentViewInfo(it)
        }

        roundEnv.getElementsAnnotatedWith(BindView::class.java).forEach {
            collectBindViewInfo(it)
        }

        roundEnv.getElementsAnnotatedWith(ClickResponder::class.java).forEach {
            collectClickResponderInfo(it)
        }
        roundEnv.getElementsAnnotatedWith(IntentValue::class.java).forEach {
            collectIntentValueInfo(it)
        }

    }

    private fun FileSpec.writeFile() {

        val kaptKotlinGeneratedDir = processingEnv.options["kapt.kotlin.generated"]
        val outputFile = File(kaptKotlinGeneratedDir).apply {
            mkdirs()
        }
        writeTo(outputFile)
    }

    private fun collectContentViewInfo(element: Element) {
        //ContentView注解的是Class，本身就是TypeElement
        val typeElement = element as TypeElement
        val className = typeElement.qualifiedName.toString()
        var fileSpecWrapper = mInjectMaps[className]
        if (fileSpecWrapper == null) {
            fileSpecWrapper = FileSpecWrapper(typeElement,messager)
        }

        typeElement.getAnnotation(ContentView::class.java).run {
            fileSpecWrapper.layoutIdStr = idStr
        }

        mInjectMaps[className] = fileSpecWrapper
    }

    private fun collectBindViewInfo(element: Element) {
        //BindView注解的是变量，element就是VariableElement
        val variableElement = element as VariableElement
        val typeElement = element.enclosingElement as TypeElement
        val className = typeElement.qualifiedName.toString()
        var fileSpecWrapper = mInjectMaps[className]
        if (fileSpecWrapper == null) {
            fileSpecWrapper = FileSpecWrapper(typeElement,messager)
        }

        variableElement.getAnnotation(BindView::class.java).run {
            fileSpecWrapper.viewMap[idStr] = variableElement
        }

        mInjectMaps[className] = fileSpecWrapper
    }

    private fun collectClickResponderInfo(element: Element) {
        //ClickResponder注解的是方法，element就是VariableElement
        val variableElement = element as ExecutableElement
        val typeElement = element.enclosingElement as TypeElement
        val className = typeElement.qualifiedName.toString()
        var fileSpecWrapper = mInjectMaps[className]
        if (fileSpecWrapper == null) {
            fileSpecWrapper = FileSpecWrapper(typeElement,messager)
        }

        variableElement.getAnnotation(ClickResponder::class.java).run {

            idStr.forEach {
                fileSpecWrapper.clickListenerMap[it] = variableElement
            }
        }

        mInjectMaps[className] = fileSpecWrapper
    }

    private fun collectIntentValueInfo(element: Element){
        //IntentValue注解的是变量，element就是VariableElement
        val variableElement = element as VariableElement
        val typeElement = element.enclosingElement as TypeElement
        val className = typeElement.qualifiedName.toString()
        var fileSpecWrapper = mInjectMaps[className]
        if (fileSpecWrapper == null) {
            fileSpecWrapper = FileSpecWrapper(typeElement,messager)
        }

        variableElement.getAnnotation(IntentValue::class.java).run {
            key.forEach {
                fileSpecWrapper.intentValueMap[it] = variableElement
            }
        }
        mInjectMaps[className] = fileSpecWrapper
    }

    //把注解类都添加进行，这个方法一看方法名就应该知道干啥的
    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(
            ContentView::class.java.canonicalName,
            BindView::class.java.canonicalName,
            ClickResponder::class.java.canonicalName,
            IntentValue::class.java.canonicalName
        )
    }

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        messager = processingEnv.messager
    }

    fun log(msg:String){
        messager.printMessage(Diagnostic.Kind.WARNING,msg)
    }
}