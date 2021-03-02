package com.fdd.mvvmgenerator.constant


fun getActivityName(name: String) = "${name}Activity"

const val EXTENSION_KT = "kt"
const val EXTENSION_JAVA = "java"
const val EXTENSION_XML = "xml"

const val AUTHOR = "AUTHOR"

const val ACTIVITY_DIR = "activity"
const val FRAGMENT_DIR = "fragment"
const val VM_DIR = "viewmodel"
const val EVENT_DIR = "event"
const val MODEL_DIR = "model"

const val USE_KOTLIN = "use_kotlin"
const val IS_ACTIVITY = "is_activity"
const val IS_LIST = "is_list"
const val USE_PREFIX = "use_prefix"


const val VIEW_IMPL_TEMPLATE_ACTIVITY_JAVA = "JavaMvvmViewActivityImpl"
const val VIEW_IMPL_TEMPLATE_FRAGMENT_JAVA = "JavaMvvmViewFragmentImpl"
const val VIEW_IMPL_TEMPLATE_ACTIVITY_KOTLIN = "KotlinMvvmViewActivityImpl"
const val VIEW_IMPL_TEMPLATE_FRAGMENT_KOTLIN = "KotlinMvvmViewFragmentImpl"

const val VM_IMPL_TEMPLATE_JAVA = "JavaMvvmVMImpl"
const val VM_IMPL_TEMPLATE_KOTLIN = "KotlinMvvmVMImpl"

const val MODEL_IMPL_TEMPLATE_JAVA = "JavaMvvmModelImpl"
const val MODEL_IMPL_TEMPLATE_KOTLIN = "KotlinMvvmModelImpl"

const val ACTIVITY_LAYOUT_TEMPLATE = "ActivityLayoutTemplate"
const val FRAGMENT_LAYOUT_TEMPLATE = "FragmentLayoutTemplate"

const val ITEM_VM_TEMPLATE_KOTLIN = "ItemVMTemplate"
const val ITEM_EVENT_TEMPLATE_KOTLIN = "ItemEventTemplate"
const val ITEM_LAYOUT_TEMPLATE = "ItemLayoutTemplate"

const val MODEL_TEMPLATE = "ModelTemplate"
const val MODEL_TEMPLATE_KOTLIN = "ModelTemplateKt"

const val MODEL_METHOD_TEMPLATE = "ModelMethodTemplate"
const val MODEL_METHOD_TEMPLATE_KOTLIN = "ModelMethodTemplateKt"

const val VIEW_MODEL_CODE_TEMPLATE = "view_model_code_template"

const val ITEM_SUB_SINGLE_ADAPTER = "item_sub_single_adapter"
const val ITEM_SUB_ADAPTER = "item_sub_adapter"


const val METHOD_NAME = "METHOD_NAME"
const val MODEL_NAME = "MODEL_NAME"
const val METHOD_PARAMS = "METHOD_PARAMS"
const val FORMAL_AND_ACTUAL_PARAMS = "FORMAL_AND_ACTUAL_PARAMS"
const val FORMAL_AND_ACTUAL_PARAMS_KT = "FORMAL_AND_ACTUAL_PARAMS_KT"
const val ACTUAL_PARAMS = "ACTUAL_PARAMS"
const val GENERIC_TYPE = "GENERIC_TYPE"
const val COMMENT = "COMMENT"
const val NEED_FAIL_EVENT = "NEED_FAIL_EVENT"
const val SHOW_LOADING = "SHOW_LOADING"
const val NEED_FAIL = "NEED_FAIL"
const val NEED_SUCCESS = "NEED_SUCCESS"
const val NEED_ITEM_EVENT = "NEED_ITEM_EVENT"
