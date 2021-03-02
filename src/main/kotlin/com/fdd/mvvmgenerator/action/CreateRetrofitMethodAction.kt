package com.fdd.mvvmgenerator.action

import com.fdd.mvvmgenerator.common.ViewModelConfig
import com.fdd.mvvmgenerator.constant.*
import com.fdd.mvvmgenerator.core.TemplateMaker
import com.fdd.mvvmgenerator.core.TemplateParamFactory.lowerFirstChar
import com.fdd.mvvmgenerator.core.createFileFromTemplate
import com.fdd.mvvmgenerator.ui.SelectViewModelDialog
import com.fdd.mvvmgenerator.utils.MethodParser
import com.fdd.mvvmgenerator.utils.PsiFileClassUtil.addFieldKt
import com.fdd.mvvmgenerator.utils.PsiFileClassUtil.createImportKt
import com.fdd.mvvmgenerator.utils.PsiFileClassUtil.createJavaImport
import com.fdd.mvvmgenerator.utils.PsiFileClassUtil.createMethod
import com.fdd.mvvmgenerator.utils.PsiFileClassUtil.createMethodKt
import com.intellij.ide.actions.CreateFileAction
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.impl.PsiElementFactoryImpl
import com.intellij.psi.impl.source.PsiJavaFileImpl
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiShortNamesCache
import com.intellij.psi.util.elementType
import org.jetbrains.kotlin.idea.core.getPackage
import org.jetbrains.kotlin.idea.core.util.toPsiDirectory
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory

/**
 * @Author:miracle
 * @Date: 2020-10-13 10:24
 * @Description:
 */
class CreateRetrofitMethodAction : AnAction() {

    private var mProject: Project? = null
    private var mPsiElementFactoryImpl: PsiElementFactoryImpl? = null
    private var mKtPsiFactory: KtPsiFactory? = null
    private var mProperties: HashMap<String, Any>? = hashMapOf()
    private var primaryDataTypeArrayKt = arrayOf("Char", "Byte","Short","Int","Long","Float","Double","Boolean","String")

    override fun actionPerformed(event: AnActionEvent) {
        mProject = event.project
        // 当前光标所在的PsiFile
        val file = event.getData(PlatformDataKeys.PSI_FILE)
        // 当前光标所在的PsiElement
        val psiElement = event.getData(PlatformDataKeys.PSI_ELEMENT)
        // 获取当前的文件
        val virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE)
        mPsiElementFactoryImpl = PsiElementFactoryImpl(mProject!!)
        mKtPsiFactory = KtPsiFactory(mProject!!)
        if (virtualFile == null) return

        // 获取当前文件的目录
        val parentDir = virtualFile.parent.parent.toPsiDirectory(mProject!!)
        //寻找/创建model目录
        val modelDir = CreateFileAction.findOrCreateSubdirectory(parentDir!!, MODEL_DIR)
        //model类的名称 eg:TestService -> TestModel
        val modelFileName = virtualFile.name.replace("Service", "Model")
        val isKt = modelFileName.endsWith(".kt")

        if (modelDir.findFile(modelFileName) == null) {
            //model类不存在,创建model类
            //模版参数
            val templateValues = hashMapOf(
                "PACKAGE_NAME" to parentDir.getPackage()?.qualifiedName,
                "SERVICE_NAME" to virtualFile.name.split(".").first().replace("Service", "")
            )
            val template = TemplateMaker.getTemplate(if (isKt) MODEL_TEMPLATE_KOTLIN else MODEL_TEMPLATE, mProject!!)
            val psiFile = createFileFromTemplate(
                modelFileName.split(".").first(),
                template!!,
                modelDir,
                null,
                false,
                templateValues,
                "author"
            )
        }
        //插入retrofit方法代码
        val modelPsiFile = modelDir.findFile(modelFileName)!!
        val children = modelPsiFile.children
        //最后一个child是whitespace，之前是Class
        var classElement: PsiElement? = null
        for (child in children) {
            if (child.elementType.toString() == "CLASS") {
                classElement = child
                break
            }
        }
        if (classElement == null) return
        //得到"}"
        val lastBrace = classElement.lastChild.lastChild
        if (isKt) {
            val template = TemplateMaker.getTemplate(MODEL_METHOD_TEMPLATE_KOTLIN, mProject!!)
            template?.isReformatCode = true
            mProperties = MethodParser.parseCurrentMethodKt(psiElement!!)
            val methodText = template!!.getText(mProperties!!)
            createMethodKt(mProject!!, mKtPsiFactory!!, methodText, classElement, lastBrace, true)
            createImportForKtClass(modelPsiFile as KtFile, mProperties!!)
        } else {
            val template = TemplateMaker.getTemplate(MODEL_METHOD_TEMPLATE, mProject!!)
            template?.isReformatCode = true
            mProperties = MethodParser.parseCurretnMethod(psiElement!!)
            val methodText = template!!.getText(mProperties!!)
            createMethod(mProject!!, mPsiElementFactoryImpl!!, methodText, classElement, lastBrace, true)
            createImportForJavaClass(modelPsiFile as PsiJavaFileImpl, mProperties!!)
        }
        //格式化
        CodeStyleManager.getInstance(mProject!!).reformat(modelPsiFile)

        //显示选择viewModel文件的弹窗
        val vmDir = virtualFile.parent.parent.findChild(VM_DIR)
        if (vmDir != null && vmDir.exists() && !vmDir.children.isNullOrEmpty()) {
            //创建dialog
            SelectViewModelDialog.build(mProject!!, vmDir.children)
                .title("Generate Fdd Retrofit Code")
                .onOkListener(object : SelectViewModelDialog.OnOkListener {
                    override fun onOk(config: ViewModelConfig) {
                        val viewModelName = config.viewModelName
                        if (viewModelName.endsWith(".java")) {
                            Messages.showErrorDialog("Java not support yet!", "Error")
                            return
                        }
                        runWriteAction {

                            //vm中插入代码
                            val vmPsiDir = CreateFileAction.findOrCreateSubdirectory(parentDir, VM_DIR)
                            val targetVmFile = vmPsiDir.findFile(viewModelName)

                            //参数
                            val properties = mProperties!!.toMutableMap()
                            properties[NEED_FAIL_EVENT] = config.needFail.toString()
                            properties[SHOW_LOADING] = config.showLoading.toString()
                            properties[MODEL_NAME] = modelFileName.split(".").first().lowerFirstChar()
                            val template = TemplateMaker.getTemplate(VIEW_MODEL_CODE_TEMPLATE, mProject!!)
                            val methodFieldText = template!!.getText(properties).replace("Integer", "Int")
                                .replace("int", "Int")
                                .replace("char", "Char")
                                .replace("byte", "Byte")
                                .replace("short", "Short")
                                .replace("long", "Long")
                                .replace("float", "Float")
                                .replace("double", "Double")
                                .replace("boolean", "Boolean")
                                .replace("Object", "Any?")


                            //找到 }
                            val vmChildren = targetVmFile!!.children
                            //最后一个child是whitespace，之前是Class
                            var vmClassElement: PsiElement? = null
                            for (child in vmChildren) {
                                if (child.elementType.toString() == "CLASS") {
                                    vmClassElement = child
                                    break
                                }
                            }
                            if (vmClassElement != null) {
                                //得到"}"
                                val vmLastBrace = vmClassElement.lastChild.lastChild
                                addFieldKt(mProject!!, mKtPsiFactory!!, methodFieldText, targetVmFile, vmLastBrace)
                            }

                            //导包
                            createImportForKtClass(targetVmFile as KtFile, mProperties!!)
                            //格式化
                            CodeStyleManager.getInstance(mProject!!).reformat(targetVmFile)
                        }
                    }
                })
                .create()

        }

    }

    /**
     * 为参数类型和返回值类型导包
     */
    private fun createImportForJavaClass(psiJavaFile: PsiJavaFileImpl, properties: Map<String, Any>) {
        val genericType = properties[GENERIC_TYPE].toString()
        val searchScope = GlobalSearchScope.allScope(mProject!!)
        val psiClasses = PsiShortNamesCache.getInstance(mProject!!).getClassesByName(genericType, searchScope)
        //导入泛型包
        if (psiClasses.isNotEmpty()) {
            createJavaImport(mProject!!, mPsiElementFactoryImpl!!, psiClasses[0], psiJavaFile)
        }
        //导入参数类型的包
        val methodParams = properties[METHOD_PARAMS] as? HashMap<String, String>
        if (!methodParams.isNullOrEmpty()) {
            methodParams.forEach { (name, type) ->
                val isLowerCase = type[0].toInt() in 97..122
                if (!isLowerCase && type != "String") {
                    val paramClasses = PsiShortNamesCache.getInstance(mProject!!).getClassesByName(type, searchScope)
                    if (paramClasses.isNotEmpty()) {
                        createJavaImport(mProject!!, mPsiElementFactoryImpl!!, paramClasses[0], psiJavaFile)
                    }
                }
            }
        }
    }

    private fun createImportForKtClass(ktFile: KtFile, properties: Map<String, Any>) {
        val genericType = properties[GENERIC_TYPE].toString()
        val searchScope = GlobalSearchScope.allScope(mProject!!)
        val psiClasses = PsiShortNamesCache.getInstance(mProject!!).getClassesByName(genericType, searchScope)
        //导入泛型包
        if (psiClasses.isNotEmpty()) {
            try {
                createImportKt(mProject!!, mKtPsiFactory!!, psiClasses[0].qualifiedName!!, ktFile)
            }catch (e:Exception) {
                e.printStackTrace()
            }
        }
        //导入参数类型的包
        val methodParams = properties[METHOD_PARAMS] as? HashMap<String, String>
        if (!methodParams.isNullOrEmpty()) {
            methodParams.forEach { (name, paramType) ->
                val isLowerCase = paramType[0].toInt() in 97..122
                if (!isLowerCase && !primaryDataTypeArrayKt.contains(paramType)) {
                    val paramClasses =
                        PsiShortNamesCache.getInstance(mProject!!).getClassesByName(paramType, searchScope)
                    if (paramClasses.isNotEmpty()) {
                        try {
                            createImportKt(mProject!!, mKtPsiFactory!!, paramClasses[0].qualifiedName!!, ktFile)
                        }catch (e:Exception) {
                            e.printStackTrace()
                        }

                    }
                }

            }
        }
    }


    override fun update(e: AnActionEvent) {
        super.update(e)
        val dataContext = e.dataContext
        val presentation = e.presentation
        val enabled = isAvailable(dataContext)
        presentation.isVisible = enabled
        presentation.isEnabled = enabled
    }

    private fun isAvailable(dataContext: DataContext): Boolean {
        val project = CommonDataKeys.PROJECT.getData(dataContext)
        return project != null
    }

}