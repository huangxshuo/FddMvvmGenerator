package com.fdd.mvvmgenerator.core

import com.fdd.mvvmgenerator.common.CreateFileConfig
import com.fdd.mvvmgenerator.constant.*
import com.fdd.mvvmgenerator.core.TemplateParamFactory.captureName
import com.intellij.psi.PsiDirectory
import org.jetbrains.android.dom.manifest.Manifest
import org.jetbrains.android.facet.AndroidFacet
import org.jetbrains.android.facet.AndroidRootUtil
import org.jetbrains.android.util.AndroidUtils
import org.jetbrains.kotlin.idea.core.getPackage

/**
 * Created by hxs
 * Description : template参数工厂
 */

object TemplateParamFactory {


    /**
     * NAME
     * PACKAGE_R R文件的报名应该是清单里面的报名
     * PACKAGE_NAME
     * VM_GENERIC_TYPE
     */
    fun getTemplateParams(
        conf: CreateFileConfig,
        tempalteName: String,
        dir: PsiDirectory,
        facet: AndroidFacet
    ): Map<String, String?> {
        val packageName = dir.parentDirectory?.getPackage()?.qualifiedName
        val moduleName = facet.module.name.split("-").last()
        val withPrefix = conf.usePrefix
        val enterName = conf.name
        val isList = conf.isList.toString()
        return when (tempalteName) {
            VIEW_IMPL_TEMPLATE_ACTIVITY_JAVA,
            VIEW_IMPL_TEMPLATE_ACTIVITY_KOTLIN ->

                hashMapOf<String, String?>(
                    "NAME" to enterName,
                    "NAME_TO_LOWER_CASE" to enterName.toLowerCase(),
                    "LAYOUT_NAME" to if (withPrefix) "${moduleName}_activity${enterName.transformEnterName()}" else "activity${enterName.transformEnterName()}",
                    "ITEM_LAYOUT_NAME" to if (withPrefix) "${moduleName}_item${enterName.transformEnterName()}" else "item${enterName.transformEnterName()}",
                    "PACKAGE_R" to getRPackage(facet),
                    "ACTIVITY_DIR" to "${packageName}.${ACTIVITY_DIR}",
                    "VM_DIR" to "${packageName}.${VM_DIR}",
                    "VM_GENERIC_TYPE" to "<${enterName}VM>",
                    "IS_LIST" to isList,
                    "WITH_PREFIX" to withPrefix.toString(),
                    "MODULE_PREFIX" to moduleName.captureName(),
                    "ITEM_EVENT_DIR" to "${packageName}.${EVENT_DIR}",
                    "ITEM_VM_DIR" to "${packageName}.${VM_DIR}"
                )

            VIEW_IMPL_TEMPLATE_FRAGMENT_KOTLIN ->
                hashMapOf<String, String?>(
                    "NAME" to enterName,
                    "NAME_TO_LOWER_CASE" to enterName.toLowerCase(),
                    "LAYOUT_NAME" to if (withPrefix) "${moduleName}_fragment${enterName.transformEnterName()}" else "fragment${enterName.transformEnterName()}",
                    "ITEM_LAYOUT_NAME" to if (withPrefix) "${moduleName}_item${enterName.transformEnterName()}" else "item${enterName.transformEnterName()}",
                    "PACKAGE_R" to getRPackage(facet),
                    "ACTIVITY_DIR" to "${packageName}.${ACTIVITY_DIR}",
                    "VM_DIR" to "${packageName}.${VM_DIR}",
                    "VM_GENERIC_TYPE" to "<${enterName}VM>",
                    "IS_LIST" to isList,
                    "WITH_PREFIX" to withPrefix.toString(),
                    "MODULE_PREFIX" to moduleName.captureName(),
                    "ITEM_EVENT_DIR" to "${packageName}.${EVENT_DIR}",
                    "ITEM_VM_DIR" to "${packageName}.${VM_DIR}"
                )

            VM_IMPL_TEMPLATE_JAVA,
            VM_IMPL_TEMPLATE_KOTLIN ->

                hashMapOf<String, String?>(
                    "NAME" to enterName,
                    "IS_LIST" to isList
                )
            ITEM_VM_TEMPLATE_KOTLIN,
            ITEM_EVENT_TEMPLATE_KOTLIN ->
                hashMapOf<String, String?>(
                    "NAME" to enterName,
                    "ITEM_VM_DIR" to "${packageName}.${VM_DIR}",
                    "IS_LIST" to isList
                )
            ITEM_SUB_ADAPTER,
            ITEM_SUB_SINGLE_ADAPTER ->
                hashMapOf<String, String?>(
                    "NAME" to enterName,
                    "ADAPTER_TYPE" to conf.adapterType,
                    "LAYOUT_HELPER_NAME" to conf.layoutHelperName,
                    "ITEM_LAYOUT_NAME" to  if (withPrefix) "${moduleName}_item${enterName.transformEnterName()}" else "item${enterName.transformEnterName()}"
                    )

            else -> hashMapOf()
        }

    }

    fun getLayoutFileTemplateParams(
        enterName: String,
        tempalteName: String,
        dir: PsiDirectory
    ): Map<String, String?> {

        val packageName = dir.parentDirectory?.getPackage()?.qualifiedName
        return when (tempalteName) {

            ACTIVITY_LAYOUT_TEMPLATE -> hashMapOf<String, String?>(
                "NAME" to enterName,
                "VM_DIR" to "${packageName}.${VM_DIR}"
            )
            FRAGMENT_LAYOUT_TEMPLATE -> hashMapOf(
                "NAME" to enterName,
                "VM_DIR" to "${packageName}.${VM_DIR}"
            )
            ITEM_LAYOUT_TEMPLATE -> hashMapOf<String, String?>(
                "NAME" to enterName,
                "ITEM_EVENT_DIR" to "${packageName}.${EVENT_DIR}",
                "ITEM_VM_DIR" to "${packageName}.${VM_DIR}"
            )
            else -> hashMapOf()
        }


    }

    //MainAAA -> activity_main_a_a_A

     fun String.transformEnterName(): String {
        val builder = StringBuilder()
        this.forEach { char ->
            if (char >= "A"[0] && char <= "Z"[0]) {
                builder.append("_")
            }
            builder.append(char)
        }
        return builder.toString().toLowerCase()
    }

     fun String.captureName(): String {
        val cs = this.toCharArray()
        cs[0] = cs[0] - 32
        return String(cs)
    }

    fun String.lowerFirstChar():String{
        val cs = this.toCharArray()
        cs[0] = cs[0] + 32
        return String(cs)
    }


    //R文件包名
    private fun getRPackage(facet: AndroidFacet): String {
        val manifestFile = AndroidRootUtil.getManifestFileForCompiler(facet) ?: return ""
        val manifest = AndroidUtils.loadDomElement(facet.module, manifestFile, Manifest::class.java)
        return manifest?.getPackage()?.value ?: ""

    }


}

