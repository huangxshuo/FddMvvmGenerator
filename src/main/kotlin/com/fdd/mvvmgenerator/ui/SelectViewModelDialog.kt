package com.fdd.mvvmgenerator.ui;

import com.fdd.mvvmgenerator.common.ViewModelConfig
import com.fdd.mvvmgenerator.constant.AUTHOR
import com.fdd.mvvmgenerator.constant.NEED_FAIL
import com.fdd.mvvmgenerator.constant.NEED_SUCCESS
import com.fdd.mvvmgenerator.constant.SHOW_LOADING
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import java.awt.Toolkit
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.*

/**
 * @Author:miracle
 * @Date: 2020-10-15 15:29
 * @Description:
 */
class SelectViewModelDialog(private val mProject: Project) : JDialog() {

    private lateinit var myPanel: JPanel
    private lateinit var viewModel: JLabel
    private lateinit var viewModelCombo: JComboBox<String>
    private lateinit var successEventCheckBox: JCheckBox
    private lateinit var failEventCheckBox: JCheckBox
    private lateinit var showLoadingCheckBox: JCheckBox
    private lateinit var buttonOK: JButton
    private lateinit var buttonCancel: JButton
    private var onOkListener: OnOkListener? = null


    init {
        contentPane = myPanel
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
        myPanel.registerKeyboardAction(
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
        val selectVmName = viewModelCombo.selectedItem as? String
        if (selectVmName.isNullOrEmpty()) {
            Messages.showErrorDialog("ViewModel not allow empty!", "Error")
            return
        }
        buttonOK.isEnabled = false
        val prop = PropertiesComponent.getInstance(mProject)
        //保存设置
        prop.setValue(NEED_SUCCESS, successEventCheckBox.isSelected)
        prop.setValue(NEED_FAIL, failEventCheckBox.isSelected)
        prop.setValue(SHOW_LOADING, showLoadingCheckBox.isSelected)

        if (onOkListener != null) {
            //j将选中的设置返回出去
            onOkListener!!.onOk(
                ViewModelConfig(
                    successEventCheckBox.isSelected,
                    failEventCheckBox.isSelected,
                    showLoadingCheckBox.isSelected,
                    selectVmName
                )
            )
        }
        dispose()
    }


    class Builder(private val dialog: SelectViewModelDialog) {

        fun title(title: String): Builder {
            dialog.title = title
            return this
        }

        fun onOkListener(listener: SelectViewModelDialog.OnOkListener): Builder {
            dialog.onOkListener = listener
            return this
        }

        fun create(): SelectViewModelDialog {
            dialog.pack()
            dialog.isVisible = true
            return dialog
        }

    }


    companion object {
        fun build(project: Project, vmFiles: Array<VirtualFile>): Builder {
            val dialog = SelectViewModelDialog(project)
            val screensize = Toolkit.getDefaultToolkit().screenSize
            val x = screensize.getWidth().toInt() / 2 - dialog.preferredSize.width / 2
            val y = screensize.getHeight().toInt() / 2 - dialog.preferredSize.height / 2
            dialog.setLocation(x, y)
            val state = PropertiesComponent.getInstance(project)
            //获取保存的设置
            val author = state.getValue(AUTHOR, "")
            val needSuccess = state.getBoolean(NEED_SUCCESS, true)
            val needFail = state.getBoolean(NEED_FAIL, true)
            val showLoading = state.getBoolean(SHOW_LOADING, true)
            dialog.viewModelCombo.isEditable = false
            dialog.successEventCheckBox.isSelected = needSuccess
            dialog.failEventCheckBox.isSelected = needFail
            dialog.showLoadingCheckBox.isSelected = showLoading
            //添加选项
            vmFiles.map { file ->
                file.name
            }
                .sorted()
                .forEach { name ->
                    dialog.viewModelCombo.addItem(name)
                }
            return Builder(dialog)
        }
    }

    interface OnOkListener {
        fun onOk(config: ViewModelConfig)
    }
}
