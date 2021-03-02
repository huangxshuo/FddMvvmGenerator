package com.fdd.mvvmgenerator.utils

import com.fdd.mvvmgenerator.constant.*
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.util.containers.isNullOrEmpty
import org.jetbrains.kotlin.psi.KtNamedFunction
import java.util.regex.Pattern

/**
 * @Author:miracle
 * @Date: 2020-10-13 10:35
 * @Description:
 */
object MethodParser {

    /**
     * 解析当前光标所在的方法
     */
    fun parseCurrentMethodKt(element: PsiElement): HashMap<String, Any>? {
        if (element is KtNamedFunction) {
            val map = hashMapOf<String, Any>()
            map[METHOD_NAME] = element.name ?: ""
            //返回值类型
            val returnType = element.typeReference?.text ?: ""
            val genericPattern = Pattern.compile("""(?<=<BaseHttpResult<).+(?=>>)""")
            val genericMatcher = genericPattern.matcher(returnType)
            //返回值的泛型类型
            if (genericMatcher.find()) {
                val genericReturnType = genericMatcher.group()
                map[GENERIC_TYPE] = genericReturnType
            }
            //方法注释
            if (element.docComment != null) {
                for (item in element.docComment!!.children) {
                    if (item.text != null && item.text.trim() != ""
                        && item.text.trim() != "/**"
                        && item.text.trim() != "*"
                    ) {
                        // 添加注释
                        map[COMMENT] = item.text.trim()
                        break
                    }
                }
            }
            //参数
            if (!element.valueParameters.isNullOrEmpty()) {
                val params = hashMapOf<String, String>()
                val javaParamsBuilder = StringBuilder()
                val ktParamsBuilder = StringBuilder()
                val actualParamsBuilder = StringBuilder()

                element.valueParameters.forEach { param ->

                    var paramType = ""
                    if (param.children.size > 1) {
                        paramType = param.children[1].text
                    }
                    params[param.name!!] = paramType
                    javaParamsBuilder.append(paramType).append(" ").append(param.name)
                    ktParamsBuilder.append(param.name).append(": ").append(paramType)
                    actualParamsBuilder.append(param.name)

                    if (param != element.valueParameters.last()) {
                        javaParamsBuilder.append(", ")
                        ktParamsBuilder.append(", ")
                        actualParamsBuilder.append(", ")
                    }

                }
                map[METHOD_PARAMS] = params
                map[FORMAL_AND_ACTUAL_PARAMS] = javaParamsBuilder.toString()
                map[FORMAL_AND_ACTUAL_PARAMS_KT] = ktParamsBuilder.toString()
                map[ACTUAL_PARAMS] = actualParamsBuilder.toString()

            }else {
                map[METHOD_PARAMS] = ""
                map[FORMAL_AND_ACTUAL_PARAMS] = ""
                map[FORMAL_AND_ACTUAL_PARAMS_KT] = ""
                map[ACTUAL_PARAMS] = ""
            }
            return map
        } else {
            Messages.showErrorDialog("Not a Method!", "Error")
            return null
        }
    }


    fun parseCurretnMethod(element: PsiElement): HashMap<String, Any>? {

        if (element is PsiMethod) {
            val map = hashMapOf<String, Any>()
            //方法名
            map[METHOD_NAME] = element.name
            //返回值类型
            val returnType = element.returnType.toString()
            val genericPattern = Pattern.compile("""(?<=<BaseHttpResult<).+(?=>>)""")
            val genericMatcher = genericPattern.matcher(returnType)
            //返回值的泛型类型
            if (genericMatcher.find()) {
                val genericReturnType = genericMatcher.group()
                map[GENERIC_TYPE] = genericReturnType
            }
            //方法注释
            if (element.docComment != null) {
                for (item in element.docComment!!.descriptionElements) {
                    if (item.text != null && item.text.trim() != ""
                        && item.text.trim() != "/**"
                        && item.text.trim() != "*"
                    ) {
                        // 添加注释
                        map[COMMENT] = item.text.trim()
                        break
                    }
                }
            }
            //参数
            if (!element.parameterList.isEmpty) {
                val params = hashMapOf<String, String>()
                val javaParamsBuilder = StringBuilder()
                val ktParamsBuilder = StringBuilder()
                val actualParamsBuilder = StringBuilder()
                for (param in element.parameterList.parameters) {
                    val paramType = param.typeElement!!.firstChild.text
                    params[param.name] = paramType
                    javaParamsBuilder.append(paramType).append(" ").append(param.name)
                    ktParamsBuilder.append(param.name).append(": ").append(paramType)
                    actualParamsBuilder.append(param.name)

                    if (param != element.parameterList.parameters.last()) {
                        javaParamsBuilder.append(", ")
                        ktParamsBuilder.append(", ")
                        actualParamsBuilder.append(", ")
                    }

                }
                map[METHOD_PARAMS] = params
                map[FORMAL_AND_ACTUAL_PARAMS] = javaParamsBuilder.toString()
                map[FORMAL_AND_ACTUAL_PARAMS_KT] = ktParamsBuilder.toString()
                map[ACTUAL_PARAMS] = actualParamsBuilder.toString()

            }else {
                map[METHOD_PARAMS] = ""
                map[FORMAL_AND_ACTUAL_PARAMS] = ""
                map[FORMAL_AND_ACTUAL_PARAMS_KT] = ""
                map[ACTUAL_PARAMS] = ""
            }
            return map
        } else {
            Messages.showErrorDialog("Not a Method!", "Error")
            return null
        }

    }


}