package com.fdd.mvvmgenerator.core

import com.fdd.mvvmgenerator.constant.*
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.ide.fileTemplates.impl.FileTemplateManagerImpl
import com.intellij.openapi.project.Project

/**
 * Created by XQ Yang on 2018/6/28  10:46.
 * Description : template管理类
 */

object TemplateMaker {


    var tpManager: FileTemplateManagerImpl? = null
    private val cacheTemplate = HashMap<String, FileTemplate>()


    private fun createTemplate(name: String, extension: String, content: String) {
        val template = FileTemplateUtil.createTemplate(
            name, extension, content,
            tpManager!!.getTemplates(FileTemplateManager.DEFAULT_TEMPLATES_CATEGORY)
        )
        template.isLiveTemplateEnabled = false
        //保存到ide中,这里就不保存了
//        tpManager.setTemplates(FileTemplateManager.DEFAULT_TEMPLATES_CATEGORY, listOf(template))
        cacheTemplate[name] = template
    }

    fun getTemplate(templateName: String, project: Project): FileTemplate? {
        if (cacheTemplate.contains(templateName)) {
            return cacheTemplate[templateName] as FileTemplate
        } else if (tpManager == null) {
            tpManager = FileTemplateManagerImpl.getInstanceImpl(project)
        }

        when (templateName) {

            VIEW_IMPL_TEMPLATE_ACTIVITY_KOTLIN -> createTemplate(
                templateName,
                EXTENSION_KT,
                TemplateConstans.VIEW_IMPL_TEMPLATE_ACTIVITY_KOTLIN_CONTENT
            )
            VIEW_IMPL_TEMPLATE_ACTIVITY_JAVA -> createTemplate(
                templateName,
                EXTENSION_JAVA,
                TemplateConstans.VIEW_IMPL_TEMPLATE_ACTIVITY_KOTLIN_CONTENT
            )


            //fragment tp
            VIEW_IMPL_TEMPLATE_FRAGMENT_KOTLIN-> createTemplate(
                templateName,
                EXTENSION_KT,
                TemplateConstans.VIEW_IMPL_TEMPLATE_FRAGMENT_KOTLIN_CONTENT
            )


            VM_IMPL_TEMPLATE_KOTLIN -> createTemplate(
                templateName,
                EXTENSION_KT,
                TemplateConstans.VM_IMPL_TEMPLATE_KOTLIN
            )
            VM_IMPL_TEMPLATE_JAVA -> createTemplate(
                templateName,
                EXTENSION_JAVA,
                TemplateConstans.VM_IMPL_TEMPLATE_KOTLIN
            )
            ACTIVITY_LAYOUT_TEMPLATE -> createTemplate(
                templateName,
                EXTENSION_XML,
                TemplateConstans.ACTIVITY_LAYOUT_TEMPLATE_CONTENT
            )

            FRAGMENT_LAYOUT_TEMPLATE-> createTemplate(
                templateName,
                EXTENSION_XML,
                TemplateConstans.FRAGMENT_LAYOUT_TEMPLATE_CONTENT
            )

            ITEM_VM_TEMPLATE_KOTLIN -> createTemplate(
                templateName,
                EXTENSION_KT,
                TemplateConstans.ITEM_VM_TEMPLATE_KOTLIN_CONTENT
            )
            ITEM_EVENT_TEMPLATE_KOTLIN -> createTemplate(
                templateName,
                EXTENSION_KT,
                TemplateConstans.ITEM_EVENT_TEMPLATE_KOTLIN_CONTENT
            )
            ITEM_LAYOUT_TEMPLATE -> createTemplate(
                templateName,
                EXTENSION_XML,
                TemplateConstans.ITEM_LAYOUT_TEMPLATE_CONTENT
            )

            MODEL_TEMPLATE -> createTemplate(
                templateName,
                EXTENSION_JAVA,
                TemplateConstans.MODEL_TEMPLATE
            )

            MODEL_TEMPLATE_KOTLIN -> createTemplate(
                templateName,
                EXTENSION_KT,
                TemplateConstans.MODEL_TEMPLATE_KT
            )


            MODEL_METHOD_TEMPLATE -> createTemplate(
                templateName,
                "",
                TemplateConstans.MODEL_METHOD_TEMPLATE
            )

            MODEL_METHOD_TEMPLATE_KOTLIN -> createTemplate(
                templateName,
                "",
                TemplateConstans.MODEL_METHOD_TEMPLATE_KT
            )

            VIEW_MODEL_CODE_TEMPLATE-> createTemplate(
                templateName,
                "",
                TemplateConstans.VIEW_MODEL_CODE_TEMPLATE
            )

            ITEM_SUB_SINGLE_ADAPTER -> createTemplate(
                templateName,
                "",
                TemplateConstans.ITEM_SUB_SINGLE_ADAPTER
            )
            ITEM_SUB_ADAPTER -> createTemplate(
                templateName,
                "",
                TemplateConstans.ITEM_SUB_ADAPTER
            )
        }

        return if (cacheTemplate.containsKey(templateName)) cacheTemplate[templateName] as FileTemplate else null
    }


}


