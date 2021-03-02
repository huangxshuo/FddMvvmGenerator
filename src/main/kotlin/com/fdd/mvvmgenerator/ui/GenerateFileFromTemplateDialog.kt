package com.fdd.mvvmgenerator.ui;

import com.fdd.mvvmgenerator.common.CreateFileConfig
import com.fdd.mvvmgenerator.constant.*
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import java.awt.Toolkit
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.*

/**
 * @Author:miracle
 * @Date: 2020-09-10 23:03
 * @Description:
 */
class GenerateFileFromTemplateDialog(private val mProject: Project) : JDialog() {

    private val NAME_CHECK_PATTERN = "[a-zA-Z]+[0-9a-zA-Z_]"
    private lateinit var myPane: JPanel
    private lateinit var buttonOK: JButton
    private lateinit var buttonCancel: JButton
    private lateinit var etName: JTextField
    private lateinit var etAuthor: JTextField
    private lateinit var mJavaRadioButton: JRadioButton
    private lateinit var mKotlinRadioButton: JRadioButton
    private lateinit var mActivityRadioButton: JRadioButton
    private lateinit var fragmentRadioButton: JRadioButton
    //private lateinit var yesRadioButton: JRadioButton
    //private lateinit var noRadioButton: JRadioButton
    private lateinit var rg_code_type : ButtonGroup
    private lateinit var rg_view_type : ButtonGroup
    //private lateinit var rg_is_list : ButtonGroup
    private lateinit var prefixCheckbox:JCheckBox
    private lateinit var isListCheckbox:JCheckBox

    private var onOkListener: OnOkListener? = null

    init {
        contentPane = myPane
        isModal = true
        rootPane.defaultButton = buttonOK
        buttonOK.addActionListener { onOK() }
        buttonCancel.addActionListener { onCancel() }
        defaultCloseOperation = DO_NOTHING_ON_CLOSE
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                onCancel()
            }
        })

        // call onCancel() on ESCAPE
        myPane.registerKeyboardAction(
            { e: ActionEvent? -> onCancel() },
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        )

    }

    /**
     * 取消
     */
    private fun onCancel() {
        // add your code here if necessary
        dispose()
    }

    /**
     * 确定
     */
    private fun onOK() {

        if (etAuthor.text.isNullOrEmpty()) {
            Messages.showErrorDialog("Author not allow empty!", "Error")
            return
        }

        if (etName.text.isNullOrEmpty()) {
            Messages.showErrorDialog("Name not allow empty!", "Error")
            return
        }

       /* if (!name.matches(Regex(NAME_CHECK_PATTERN))) {
            Messages.showErrorDialog("An illegal name!", "Error")
            return
        }*/

        if (mJavaRadioButton.isSelected) {
            Messages.showWarningDialog("Java not support yet!", "Warning")
            return
        }

        buttonOK.isEnabled = false
        val prop = PropertiesComponent.getInstance(mProject)
        //保存设置
        prop.setValue(AUTHOR, etAuthor.text)
        prop.setValue(USE_KOTLIN, mKotlinRadioButton.isSelected)
        prop.setValue(IS_ACTIVITY, mActivityRadioButton.isSelected)
        prop.setValue(IS_LIST, isListCheckbox.isSelected)
        prop.setValue(USE_PREFIX, prefixCheckbox.isSelected)
        if (onOkListener != null) {
            //j将选中的设置返回出去
            onOkListener!!.onOk(
                CreateFileConfig(
                    etAuthor.text,
                    etName.text,
                    mKotlinRadioButton.isSelected,
                    mActivityRadioButton.isSelected,
                    isListCheckbox.isSelected,
                    prefixCheckbox.isSelected,
                    prop
                )
            )
        }
        dispose()
    }


    class Builder(private val dialog: GenerateFileFromTemplateDialog) {

        fun title(title: String): Builder {
            dialog.title = title
            return this
        }

        fun onOkListener(listener: OnOkListener): Builder {
            dialog.onOkListener = listener
            return this
        }

        fun create(): GenerateFileFromTemplateDialog {
            dialog.pack()
            dialog.isVisible = true
            return dialog
        }

    }

    companion object {
        fun build(project: Project): Builder {
            val dialog = GenerateFileFromTemplateDialog(project)
            val screensize = Toolkit.getDefaultToolkit().screenSize
            val x = screensize.getWidth().toInt() / 2 - dialog.preferredSize.width / 2
            val y = screensize.getHeight().toInt() / 2 - dialog.preferredSize.height / 2
            dialog.setLocation(x, y)
            val state = PropertiesComponent.getInstance(project)
            //获取保存的设置
            val author = state.getValue(AUTHOR, "")
            val useKt = state.getBoolean(USE_KOTLIN, true)
            val isAct = state.getBoolean(IS_ACTIVITY, true)
            val isList = state.getBoolean(IS_LIST, false)
            val usePrefix = state.getBoolean(IS_LIST, false)
            dialog.etAuthor.text = author
            dialog.mActivityRadioButton.isSelected = isAct
            dialog.fragmentRadioButton.isSelected = !isAct
            dialog.mJavaRadioButton.isSelected = !useKt
            dialog.mKotlinRadioButton.isSelected = useKt
            dialog.isListCheckbox.isSelected = isList
            dialog.prefixCheckbox.isSelected = usePrefix
            return Builder(dialog)
        }
    }

    interface OnOkListener {
        fun onOk(createFileConfig: CreateFileConfig)
    }

}
