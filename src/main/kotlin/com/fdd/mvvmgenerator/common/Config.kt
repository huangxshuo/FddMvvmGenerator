package com.fdd.mvvmgenerator.common

import com.intellij.ide.util.PropertiesComponent

/**
 * 选项配置
 */
data class CreateFileConfig(
    var author: String,
    var name: String,
    var isKt: Boolean = true,
    var isActivity: Boolean = true,
    var isList: Boolean = false,
    var usePrefix: Boolean = false,
    var prop: PropertiesComponent,
    var layoutHelperName:String = "",
    var adapterType: String = ""
)

data class ViewModelConfig(
    var needSuccess: Boolean = true,
    var needFail: Boolean = true,
    var showLoading: Boolean = true,
    var viewModelName:String
){
    override fun toString(): String {
        return "ViewModelConfig(needSuccess=$needSuccess, needFail=$needFail, showLoading=$showLoading, viewModelName='$viewModelName')"
    }
}

data class ItemAdapterConfig(
    var adapterName:String,
    var adapterType:String,
    var layoutHelperType:String,
    var usePrefix: Boolean = false,
    var needItemEvent: Boolean = true
){
    override fun toString(): String {
        return "ItemAdapterConfig(adapterName='$adapterName', adapterType='$adapterType', layoutHelperType='$layoutHelperType', usePrefix=$usePrefix, needItemEvent=$needItemEvent)"
    }
}