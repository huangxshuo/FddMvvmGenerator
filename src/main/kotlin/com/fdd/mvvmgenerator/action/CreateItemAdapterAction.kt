package com.fdd.mvvmgenerator.action

import com.fdd.mvvmgenerator.common.CreateFileConfig
import com.fdd.mvvmgenerator.common.ItemAdapterConfig
import com.fdd.mvvmgenerator.constant.*
import com.fdd.mvvmgenerator.core.TemplateMaker
import com.fdd.mvvmgenerator.core.TemplateParamFactory
import com.fdd.mvvmgenerator.core.createFileFromTemplate
import com.fdd.mvvmgenerator.core.createLayoutFile
import com.fdd.mvvmgenerator.ui.GenerateItemAdapterDialog
import com.fdd.mvvmgenerator.utils.showNotify
import com.intellij.ide.actions.CreateFileAction
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.WriteActionAware
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.kotlin.idea.core.util.toPsiDirectory
import org.jetbrains.kotlin.psi.KtFile

/**
 * @Author:miracle
 * @Date: 2020-10-30 11:27
 * @Description:
 */
class CreateItemAdapterAction : AnAction("Generate Item Adapter"), WriteActionAware {
    private var mProject: Project? = null
    private lateinit var mProp: PropertiesComponent


    override fun actionPerformed(event: AnActionEvent) {
        val dataContext = event.dataContext
        mProject = CommonDataKeys.PROJECT.getData(dataContext)
        mProp = PropertiesComponent.getInstance(mProject!!)
        // 当前光标所在的PsiFile
        val file = event.getData(PlatformDataKeys.PSI_FILE)
        // 当前光标所在的PsiElement
        val psiElement = event.getData(PlatformDataKeys.PSI_ELEMENT)
        // 获取当前的文件
        val virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE) ?: return
        val mEditor = event.getData(PlatformDataKeys.EDITOR) ?: return
        val caret = mEditor.caretModel.currentCaret
        val document = mEditor.document

        //当前是一个Activity/Fragment
        //找到功能模块包名
        val functionDir = virtualFile.parent.toPsiDirectory(mProject!!) ?: return
        //创建dialog
        GenerateItemAdapterDialog.build(mProject!!)
            .title("Generate Item Adapter")
            .onOkListener(object : GenerateItemAdapterDialog.OnOkListener {
                override fun onOk(config: ItemAdapterConfig) {
                    //找到当前的module
                    val module = ModuleUtil.findModuleForFile(functionDir.virtualFile, mProject!!)
                    val facet = AndroidFacet.getInstance(module!!)
                    val fileConf = CreateFileConfig(
                        "", config.adapterName,
                        isKt = true,
                        isActivity = false,
                        isList = false,
                        usePrefix = config.usePrefix,
                        prop = mProp,
                        layoutHelperName = config.layoutHelperType,
                        adapterType = config.adapterType
                    )
                    runWriteAction {
                        createKtFile(fileConf, functionDir, facet!!)
                        //插入itemAdapter代码
                        val template: FileTemplate?
                        if (fileConf.adapterType == "ReDataBindingSubAdapter") {
                            template = TemplateMaker.getTemplate(ITEM_SUB_ADAPTER, mProject!!)
                        } else {
                            template = TemplateMaker.getTemplate(ITEM_SUB_SINGLE_ADAPTER, mProject!!)
                        }
                        if (template == null) return@runWriteAction
                        val params = TemplateParamFactory.getTemplateParams(
                            fileConf,
                            ITEM_SUB_SINGLE_ADAPTER,
                            functionDir,
                            facet
                        )
                        val itemAdapterContent = template.getText(params)
                        //将内容粘贴到光标所在的位置
                        WriteCommandAction.runWriteCommandAction(mProject!!) {
                            document.insertString(caret.offset, itemAdapterContent)
                        }
                        showNotify("3 files generated successfully!", mProject)

                    }
                }
            })
            .create()


    }

    /**
     * 生成kotlin文件
     */
    private fun createKtFile(conf: CreateFileConfig, dir: PsiDirectory, facet: AndroidFacet) {

        //当前选中的目录下寻找/创建activity目录
        val vmDir = CreateFileAction.findOrCreateSubdirectory(dir, VM_DIR)
        val eventDir = CreateFileAction.findOrCreateSubdirectory(dir, EVENT_DIR)

        //创建itemVM
        val itemVmKt = createFile(
            conf,
            ITEM_VM_TEMPLATE_KOTLIN,
            vmDir,
            "${conf.name}ItemVM",
            facet
        )

        //创建itemEvent
        val itemEventKt = createFile(
            conf,
            ITEM_EVENT_TEMPLATE_KOTLIN,
            eventDir,
            "${conf.name}ItemEvent",
            facet
        )

        //item layout
        createLayoutFile(conf, ITEM_LAYOUT_TEMPLATE, itemVmKt, mProject!!, facet)
    }

    private fun createFile(
        conf: CreateFileConfig,
        templateName: String,
        dir: PsiDirectory,
        fileName: String = conf.name,
        facet: AndroidFacet
    ): Pair<PsiFile?, PsiClass?> {

        var clazz: PsiClass? = null
        val template = TemplateMaker.getTemplate(templateName, mProject!!) ?: return null to null
        val liveTemplateDefaultValues = TemplateParamFactory.getTemplateParams(conf, templateName, dir, facet)
        val psiFile = createFileFromTemplate(
            fileName,
            template,
            dir,
            null,
            false,
            liveTemplateDefaultValues,
            mProp.getValue(AUTHOR)
        )
        if (psiFile is PsiJavaFile) {
            if (psiFile.classes.isEmpty()) {
                return psiFile to null
            }
            clazz = psiFile.classes[0]
        } else if (psiFile is KtFile) {
            if (psiFile.classes.isEmpty()) {
                return psiFile to null
            }
            clazz = psiFile.classes[0]
        }

        return psiFile to clazz
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