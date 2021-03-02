package com.fdd.mvvmgenerator.utils

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.psi.*
import com.intellij.psi.impl.PsiElementFactoryImpl
import com.intellij.psi.impl.source.PsiClassImpl
import com.intellij.psi.impl.source.PsiJavaFileImpl
import com.intellij.psi.impl.source.tree.java.PsiPackageStatementImpl
import org.jetbrains.kotlin.idea.kdoc.KDocElementFactory
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.ImportPath

/**
 * @Author:miracle
 * @Date: 2020-10-12 11:17
 * @Description:
 */
object PsiFileClassUtil {

    /**
     * 获取java类包名
     */
    fun getPackageName(psiJavaFile: PsiFile): String {
        val psiElements = psiJavaFile.children
        psiElements.forEach { psiElement ->
            if (psiElement is PsiPackageStatementImpl) {
                return psiElement.packageName
            }
        }
        return ""
    }


    /**
     * 获取类中的变量名及类型
     */
    fun getFieldsWithType(psiClass: PsiClassImpl): Map<String, String> {
        val map = hashMapOf<String, String>()
        val allFields = psiClass.allFields
        allFields.forEach { psiField ->
            map[psiField.name] = psiField.type.presentableText
        }
        return map
    }


    /**
     * 获取类的构造函数
     */
    fun getConstructors(psiClass: PsiClassImpl): List<String> {
        val list = arrayListOf<String>()
        val constructors = psiClass.constructors
        constructors.forEach { constructor ->
            if (constructor.body != null) {
                list.add(constructor.text)
            }
        }
        return list
    }

    /**
     * 获取类中的所有方法
     */
    fun getMethods(psiClass: PsiClassImpl): List<String> {
        val list = arrayListOf<String>()
        val methods = psiClass.methods
        methods.forEach { method ->
            if (method.body != null) {
                list.add(method.text)
            }
        }
        return list
    }

    /**
     * 根据方法名查找方法
     */
    fun getMethodsByName(psiClass: PsiClassImpl, methodName: String): List<String> {
        val methods = arrayListOf<String>()
        val psiMethods = psiClass.methods
        psiMethods.forEach { method ->
            if (method.body != null && method.text.contains(methodName)) {
                methods.add(method.text)
            }
        }
        return methods
    }

    /**
     * 为类添加一个变量,在anchor之前
     */
    fun addField(
        project: Project,
        psiElementFactoryImpl: PsiElementFactoryImpl,
        text: String,
        context: PsiElement,
        anchor: PsiElement?
    ): PsiElement {
        return WriteCommandAction.runWriteCommandAction(project,
            Computable<PsiElement> {
                val psiField = psiElementFactoryImpl.createFieldFromText(text, context)
                if (anchor != null) {
                    context.addBefore(psiField, anchor.lastChild)
                } else {
                    context.add(psiField)
                }
            })
    }


    /**
     * 为类添加一个变量,在anchor之后
     */
    fun addFieldAfter(
        project: Project,
        psiElementFactoryImpl: PsiElementFactoryImpl,
        text: String,
        context: PsiElement,
        anchor: PsiElement?
    ): PsiElement? {
        return WriteCommandAction.runWriteCommandAction(project,
            Computable<PsiElement> {
                val psiField: PsiField = psiElementFactoryImpl.createFieldFromText(text, context)
                if (anchor != null) {
                    context.addAfter(psiField, anchor)
                } else {
                    context.add(psiField)
                }
            })
    }

    /**
     * 为类添加一个变量,在anchor之前/后,并换行
     */
    fun addFieldWithWhiteSpace(
        project: Project,
        context: PsiElement,
        anchor: PsiElement?,
        isBefore: Boolean
    ): PsiElement {
        return WriteCommandAction.runWriteCommandAction<PsiElement>(project) {
            val whiteSpace = PsiParserFacade.SERVICE.getInstance(project).createWhiteSpaceFromText("\n")
            if (isBefore) {
                context.addBefore(whiteSpace, anchor)
            } else {
                context.addAfter(whiteSpace, anchor)
            }
        }
    }

    /**
     * 给方法添加注释
     */
    fun addCommentToMethod(
        commentText: String,
        project: Project,
        psiElementFactoryImpl: PsiElementFactoryImpl,
        context: PsiElement,
        anchor: PsiElement?,
        isBefore: Boolean
    ): PsiElement {
        return WriteCommandAction.runWriteCommandAction<PsiElement>(project) {
            val comment = psiElementFactoryImpl.createCommentFromText(commentText, context)
            if (isBefore) {
                context.addBefore(comment, anchor)
            } else {
                context.addAfter(comment, anchor)
            }
        }
    }

    /**
     * 创建一个方法
     * @param text
     * @param context
     * @return
     */
    fun createMethod(
        project: Project,
        psiElementFactoryImpl: PsiElementFactoryImpl,
        text: String,
        context: PsiElement,
        anchor: PsiElement?,
        isBefore: Boolean
    ): PsiElement? {
        return WriteCommandAction.runWriteCommandAction<PsiElement>(
            project
        ) {
            val psiMethod = psiElementFactoryImpl.createMethodFromText(text, context)
            if (anchor != null) {
                if (isBefore) {
                    context.addBefore(psiMethod, anchor)
                } else {
                    context.addAfter(psiMethod, anchor)
                }
            } else {
                context.add(psiMethod)
            }
        }
    }


    /**
     * 创建并插入Import
     * @param psiClass
     * @param psiJavaFile
     * @return
     */
    fun createJavaImport(
        project: Project,
        psiElementFactoryImpl: PsiElementFactoryImpl,
        psiClass: PsiClass,
        psiJavaFile: PsiJavaFileImpl
    ): PsiImportStatement {
        return WriteCommandAction.runWriteCommandAction<PsiImportStatement>(project) {
            val importStatement: PsiImportStatement = psiElementFactoryImpl.createImportStatement(psiClass)
            val importList = psiJavaFile.importList
            if (importList!!.findSingleImportStatement(psiClass.name) == null) {
                psiJavaFile.importList!!.add(importStatement)
            }
            importStatement
        }
    }


    /*kotlin 方法 */

    fun addFieldKt(
        project: Project,
        ktPsiFactory: KtPsiFactory,
        text: String,
        context: PsiElement,
        anchor: PsiElement?

    ): PsiElement {
        return WriteCommandAction.runWriteCommandAction<PsiElement>(
            project
        ) {
            //KtProperty psiField = mKtPsiFactory.createProperty(text);
            val ktBlockCodeFragment: KtBlockCodeFragment = ktPsiFactory.createBlockCodeFragment(text, context)
            if (anchor != null) {
                context.addBefore(ktBlockCodeFragment, anchor)
            } else {
                context.add(ktBlockCodeFragment)
            }
        }
    }

    fun createMethodKt(
        project: Project,
        ktPsiFactory: KtPsiFactory,
        text: String,
        context: PsiElement,
        anchor: PsiElement?,
        isBefore: Boolean
    ): PsiElement {
        return WriteCommandAction.runWriteCommandAction<PsiElement>(
            project
        ) {
            //val psiMethod: KtNamedFunction = ktPsiFactory.createFunction(text)
            val methodCodeFragment  = ktPsiFactory.createBlockCodeFragment(text, context)

            if (anchor != null) {
                if (isBefore) {
                    context.addBefore(methodCodeFragment, anchor)
                } else {
                    context.addAfter(methodCodeFragment, anchor)
                }
            } else {
                context.add(methodCodeFragment)
            }
        }
    }

    fun createImportKt(
        project: Project,
        ktPsiFactory: KtPsiFactory,
        ktClass: String,
        ktFile: KtFile
    ): PsiImportStatement {
        return WriteCommandAction.runWriteCommandAction<PsiImportStatement>(project) {
            val fqName = FqName(ktClass)
            val importPath = ImportPath(fqName, false)
            val classImport: KtImportDirective = ktPsiFactory.createImportDirective(importPath)
            val importList = ktFile.importList
            var hasImport = false
            for (ktImportDirective in importList!!.imports) {
                if (classImport.importPath == ktImportDirective.importPath) {
                    hasImport = true
                    break
                }
            }
            if (!hasImport) {
                ktFile.importList!!.add(classImport)
            }
            null
        }

    }

    /**
     * 给方法添加注释
     */
    fun addCommentToMethodKt(
        project: Project,
        commentText: String,
        method: PsiElement
    ): PsiElement {
        return WriteCommandAction.runWriteCommandAction<PsiElement>(project) {
            val kDocElementFactory = KDocElementFactory(project)
            val kDoc = kDocElementFactory.createKDocFromText(commentText)
            //PsiComment comment = mKtPsiFactory.createComment(commentText);
            method.addBefore(kDoc, method.firstChild)
        }
    }


}