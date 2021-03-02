package com.fdd.mvvmgenerator.action

import com.fdd.mvvmgenerator.common.CreateFileConfig
import com.fdd.mvvmgenerator.common.Icons
import com.fdd.mvvmgenerator.constant.*
import com.fdd.mvvmgenerator.core.*
import com.fdd.mvvmgenerator.ui.GenerateFileFromTemplateDialog
import com.fdd.mvvmgenerator.utils.showNotify
import com.intellij.ide.actions.CreateFileAction
import com.intellij.ide.fileTemplates.actions.CreateFromTemplateActionBase
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.WriteActionAware
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.kotlin.psi.KtFile

/**
 * @Author:miracle
 * @Date: 2020-09-10 14:49
 * @Description:
 */
class CreateFileFromTemplateAction : AnAction("Generate Fdd Mvvm Code", "auto generate fdd mvvm code", Icons.FDD_ICON),
    WriteActionAware {

    private var mProject: Project? = null
    private lateinit var mProp: PropertiesComponent


    override fun actionPerformed(event: AnActionEvent) {
        val dataContext = event.dataContext
        val view = LangDataKeys.IDE_VIEW.getData(dataContext) ?: return
        mProject = CommonDataKeys.PROJECT.getData(dataContext)
        val dir = view.orChooseDirectory
        if (dir == null || mProject == null) return
        //创建dialog
        GenerateFileFromTemplateDialog.build(mProject!!)
            .title("Generate Fdd Mvvm Code")
            .onOkListener(object : GenerateFileFromTemplateDialog.OnOkListener {
                override fun onOk(createFileConfig: CreateFileConfig) {
                    mProp = createFileConfig.prop
                    //找到当前的module
                    val module = ModuleUtil.findModuleForFile(dir.virtualFile, mProject!!)
                    val facet = AndroidFacet.getInstance(module!!)
                    runWriteAction {
                        if (createFileConfig.isKt) {
                            createKtFile(createFileConfig, dir, facet!!)

                        } else {

                        }
                    }
                }
            })
            .create()


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
        val view = LangDataKeys.IDE_VIEW.getData(dataContext)
        return project != null && view != null && view.directories.isNotEmpty()
    }


    /**
     * 生成kotlin文件
     */
    private fun createKtFile(conf: CreateFileConfig, dir: PsiDirectory, facet: AndroidFacet) {


        //当前选中的目录下寻找/创建activity目录
        val activityDir = CreateFileAction.findOrCreateSubdirectory(dir, ACTIVITY_DIR)
        val fragmentDir = CreateFileAction.findOrCreateSubdirectory(dir, FRAGMENT_DIR)
        val vmDir = CreateFileAction.findOrCreateSubdirectory(dir, VM_DIR)
        val itemEventDir = CreateFileAction.findOrCreateSubdirectory(dir, EVENT_DIR)

        if (conf.isActivity) {

            //创建activity
            val activityKt = createFile(
                conf,
                VIEW_IMPL_TEMPLATE_ACTIVITY_KOTLIN,
                activityDir,
                "${conf.name}Activity",
                facet
            )
            //创建vm
            createFile(
                conf,
                VM_IMPL_TEMPLATE_KOTLIN,
                vmDir,
                "${conf.name}VM",
                facet
            )
            ComponentRegister.registerActivity(
                mProject!!,
                activityKt.second,
                JavaDirectoryService.getInstance().getPackage(dir),
                facet,
                ""
            )

            //创建layout文件
            createLayoutFile(conf, ACTIVITY_LAYOUT_TEMPLATE, activityKt, mProject!!, facet)

            //打开activity
            FileEditorManager.getInstance(mProject!!).openFile(activityKt.first!!.virtualFile, true)

        } else {//fragment
            //创建fragment
            val fragmentKt = createFile(
                conf,
                VIEW_IMPL_TEMPLATE_FRAGMENT_KOTLIN,
                fragmentDir,
                "${conf.name}Fragment",
                facet
            )
            //创建vm
            createFile(
                conf,
                VM_IMPL_TEMPLATE_KOTLIN,
                vmDir,
                "${conf.name}VM",
                facet
            )

            //创建layout文件
            createLayoutFile(conf, FRAGMENT_LAYOUT_TEMPLATE, fragmentKt, mProject!!, facet)
            FileEditorManager.getInstance(mProject!!).openFile(fragmentKt.first!!.virtualFile, true)
        }

        //创建itemVM,itemEvent,item_layout
        if (conf.isList) {
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
                itemEventDir,
                "${conf.name}ItemEvent",
                facet
            )

            //item layout
            createLayoutFile(conf, ITEM_LAYOUT_TEMPLATE, itemVmKt, mProject!!, facet)
        }

        showNotify("${if (conf.isList) 6 else 3} files generated successfully!", mProject)

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
        val liveTemplateDefaultValues =
            TemplateParamFactory.getTemplateParams(conf, templateName, dir, facet)
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
}