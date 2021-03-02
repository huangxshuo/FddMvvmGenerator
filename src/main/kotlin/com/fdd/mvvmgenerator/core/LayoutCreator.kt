package com.fdd.mvvmgenerator.core

import com.android.tools.idea.util.dependsOnAndroidx
import com.fdd.mvvmgenerator.common.CreateFileConfig
import com.fdd.mvvmgenerator.constant.ACTIVITY_LAYOUT_TEMPLATE
import com.fdd.mvvmgenerator.constant.FRAGMENT_LAYOUT_TEMPLATE
import com.fdd.mvvmgenerator.constant.ITEM_LAYOUT_TEMPLATE
import com.fdd.mvvmgenerator.core.TemplateMaker.getTemplate
import com.fdd.mvvmgenerator.core.TemplateParamFactory.getLayoutFileTemplateParams
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.*
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import org.jetbrains.android.dom.manifest.Manifest
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.android.facet.AndroidRootUtil
import org.jetbrains.android.util.AndroidResourceUtil
import org.jetbrains.android.util.AndroidUtils


@Throws(Exception::class)
fun createLayoutFile(
    conf: CreateFileConfig,
    templateName: String,
    pair: Pair<PsiFile?, PsiClass?>,
    project: Project,
    facet: AndroidFacet
): PsiElement? {
    return if (pair.second == null) {
        null
    } else {
        val manifestFile = AndroidRootUtil.getManifestFileForCompiler(facet) ?: return null
        val manifest = AndroidUtils.loadDomElement(facet.module, manifestFile, Manifest::class.java)
        val appPackage = manifest?.getPackage()?.value
        if (!appPackage.isNullOrEmpty()) {
            ApplicationManager.getApplication().invokeLater {
                LocalFileSystem.getInstance().findFileByPath(AndroidRootUtil.getResourceDirPath(facet) ?: "")
                    ?.let { file ->
                        PsiManager.getInstance(project).findDirectory(file)?.let { psiDir ->
                            /* createLayoutFileForActivityOrFragment(
                                 conf,
                                 facet,
                                 element,
                                 appPackage,
                                 psiDir,
                                 isJava,
                                 isActivity
                             )*/
                            createLayoutFileFromTemplate(project, conf, templateName, facet, pair, psiDir)
                        }
                    }

            }
        }
        pair.second
    }
}


fun createLayoutFileFromTemplate(
    project: Project,
    conf: CreateFileConfig,
    templateName: String,
    facet: AndroidFacet,
    pair: Pair<PsiFile?, PsiClass?>,
    resDirectory: PsiDirectory

) {
    val ktFile = pair.first
    val withPrefix = conf.usePrefix
    val moduleName = facet.module.name.split("-").last()
    val className = pair.second?.name
    if (!className.isNullOrEmpty()) {

        val baneBuilder = StringBuilder()
        conf.name.forEach { char ->
            if (char >= "A"[0] && char <= "Z"[0]) {
                baneBuilder.append("_")
            }
            baneBuilder.append(char)
        }

        val layoutFileName = when (templateName) {
            ACTIVITY_LAYOUT_TEMPLATE ->
                if (withPrefix) {
                    "${moduleName}_activity${baneBuilder.toString().toLowerCase()}"
                }else {
                    "activity${baneBuilder.toString().toLowerCase()}"
                }

            FRAGMENT_LAYOUT_TEMPLATE ->
                if (withPrefix) {
                    "${moduleName}_fragment${baneBuilder.toString().toLowerCase()}"
                }else {
                    "fragment${baneBuilder.toString().toLowerCase()}"
                }
            ITEM_LAYOUT_TEMPLATE ->
                if (withPrefix) {
                    "${moduleName}_item${baneBuilder.toString().toLowerCase()}"
                }else {
                    "item${baneBuilder.toString().toLowerCase()}"
                }
            else -> ""
        }


        val layoutDir = resDirectory.findSubdirectory("layout")!!
        val layoutTemplate = getTemplate(templateName, project)
        val layoutTemplateValues = getLayoutFileTemplateParams(conf.name, templateName, ktFile!!.parent!!)

        createFileFromTemplate(
            layoutFileName,
            layoutTemplate!!,
            layoutDir,
            null,
            false,
            layoutTemplateValues,
            conf.author
        )
    }
}

fun createLayoutFileForActivityOrFragment(
    conf: CreateFileConfig,
    facet: AndroidFacet,
    activityClass: PsiClass,
    appPackage: String,
    resDirectory: PsiDirectory,
    isJava: Boolean,
    isActivity: Boolean
) {
    if (!facet.isDisposed && activityClass.isValid) {
        val className = activityClass.name
        if (className != null) {

            val baneBuilder = StringBuilder()
            conf.name.forEach { char ->
                if (char >= "A"[0] && char <= "Z"[0]) {
                    baneBuilder.append("_")
                }
                baneBuilder.append(char)
            }

            val layoutFileOriginName = if (isActivity)
                "activity${baneBuilder.toString().toLowerCase()}"
            else
                "fragment${baneBuilder.toString().toLowerCase()}"

            val rootElementName =
                if (facet.module.dependsOnAndroidx())
                    "LinearLayout"
                else
                    "LinearLayout"

            val layoutFile = AndroidResourceUtil.createFileResource(
                layoutFileOriginName,
                resDirectory.findSubdirectory("layout")!!,
                rootElementName,
                "layout",
                false
            )

//            val layoutFileName = layoutFile?.name
//            val onCreateMethods = activityClass.findMethodsByName("getLayoutId", false)//todo 生成viewBinding
//            if (onCreateMethods.size != 1) {
//                return
//            }
//            if (activityClass is KtUltraLightClass){
//
//                val psiMethod = activityClass.kotlinOrigin.findFunctionByName("getLayoutId") as KtNamedFunction
//
//                val fieldName = AndroidResourceUtil.getRJavaFieldName(FileUtil.getNameWithoutExtension(layoutFileName))
//                val layoutFieldRef = "$appPackage.R.layout.$fieldName"
//                getKtStatement(psiMethod, layoutFieldRef, false)
//
//            }
//            val onCreateMethod = onCreateMethods[0]
//            val fieldName = AndroidResourceUtil.getRJavaFieldName(FileUtil.getNameWithoutExtension(layoutFileName))
//            val layoutFieldRef = "$appPackage.R.layout.$fieldName"
//            getKtStatement(onCreateMethod, layoutFieldRef, isJava)
        }
    }
}


fun addInflateStatement(body: PsiCodeBlock, layoutFieldRef: String, isJava: Boolean) {
    val project = body.project
    val statements = body.statements
    if (statements.size == 1) {
        val statement = statements[0]
        WriteCommandAction.writeCommandAction(project, body.containingFile).run<Throwable> {
            val newStatement = PsiElementFactory.getInstance(project).createStatementFromText(
                "return $layoutFieldRef${if (isJava) ";" else ""}", body
            )
            statement.replace(newStatement)
            JavaCodeStyleManager.getInstance(project).shortenClassReferences(body)
            CodeStyleManager.getInstance(project).reformat(body)
        }
    }
}

fun getKtStatement(method: PsiMethod, layoutFieldRef: String, isJava: Boolean) {
    val project = method.project
    WriteCommandAction.writeCommandAction(project, method.containingFile).run<Throwable> {
        val newStatement = PsiElementFactory.getInstance(project).createStatementFromText(
            "return $layoutFieldRef${if (isJava) ";" else ""}", method
        )
        method.add(newStatement)
        JavaCodeStyleManager.getInstance(project).shortenClassReferences(method)
        CodeStyleManager.getInstance(project).reformat(method)
    }
}

