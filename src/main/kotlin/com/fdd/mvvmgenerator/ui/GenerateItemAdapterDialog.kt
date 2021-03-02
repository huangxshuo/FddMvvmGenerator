package com.fdd.mvvmgenerator.ui

import com.fdd.mvvmgenerator.common.ItemAdapterConfig
import com.fdd.mvvmgenerator.constant.NEED_ITEM_EVENT
import com.fdd.mvvmgenerator.constant.USE_PREFIX
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.jetbrains.cidr.ui.ItemState
import java.awt.Toolkit
import java.awt.event.*
import javax.swing.*

/**
 * @Author:miracle
 * @Date: 2020-10-29 15:02
 * @Description:
 */
class GenerateItemAdapterDialog(private val mProject: Project) : JDialog() {
    private lateinit var myPanel: JPanel
    private lateinit var etName: JTextField
    private lateinit var layoutHelperCb: JComboBox<String>
    private lateinit var adapterTypeCb: JComboBox<String>
    private lateinit var moduleNameAsPrefixCheckBox: JCheckBox
    private lateinit var needItemEventCheckBox: JCheckBox
    private lateinit var itemEventPanel: JPanel
    private lateinit var buttonOK: JButton
    private lateinit var buttonCancel: JButton
    private var onOkListener: OnOkListener? = null




    init {
        contentPane = myPanel
        isModal = true
        rootPane.defaultButton = buttonOK
        buttonOK.addActionListener { onOK() }
        buttonCancel.addActionListener { onCancel() }
        defaultCloseOperation = JDialog.DO_NOTHING_ON_CLOSE
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
        itemEventPanel.isVisible = false
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

        if (etName.text.isNullOrEmpty()) {
            Messages.showErrorDialog("Adapter name not allow empty!", "Error")
            return
        }

        val adapterTypeName = adapterTypeCb.selectedItem as? String
        if (adapterTypeName.isNullOrEmpty()) {
            Messages.showErrorDialog("Adapter type not allow empty!", "Error")
            return
        }

        val layoutHelperName = layoutHelperCb.selectedItem as? String
        if (layoutHelperName.isNullOrEmpty()) {
            Messages.showErrorDialog("Layout helper not allow empty!", "Error")
            return
        }

        buttonOK.isEnabled = false
        val prop = PropertiesComponent.getInstance(mProject)
        //保存设置
        prop.setValue(USE_PREFIX, moduleNameAsPrefixCheckBox.isSelected)
        prop.setValue(NEED_ITEM_EVENT, needItemEventCheckBox.isSelected)

        if (onOkListener != null) {
            //j将选中的设置返回出去
            onOkListener!!.onOk(
                ItemAdapterConfig(
                    etName.text,
                    adapterTypeCb.selectedItem as String,
                    layoutHelperCb.selectedItem as String,
                    moduleNameAsPrefixCheckBox.isSelected,
                    needItemEventCheckBox.isSelected
                )
            )
        }
        dispose()
    }


    class Builder(private val dialog: GenerateItemAdapterDialog) {

        fun title(title: String): Builder {
            dialog.title = title
            return this
        }

        fun onOkListener(listener: OnOkListener): Builder {
            dialog.onOkListener = listener
            return this
        }

        fun create(): GenerateItemAdapterDialog {
            dialog.pack()
            dialog.isVisible = true
            return dialog
        }

    }


    companion object {

        val adapterTypeArray = arrayOf(
            "ReDataBindingSubAdapter",
            "ReDataBindingSubSingleAdapter",
            "ReDataBindingSubMultiAdapter"
        )
        val layoutHelperArray = arrayOf(
            "AbstractFullFillLayoutHelper",
            "BaseLayoutHelper",
            "ColumnLayoutHelper",
            "DefaultLayoutHelper",
            "FixAreaLayoutHelper",
            "FixLayoutHelper",
            "FloatLayoutHelper",
            "GridLayoutHelper",
            "LinearLayoutHelper",
            "MarginLayoutHelper",
            "OnePlusNLayoutHelper",
            "OnePlusNLayoutHelperEx",
            "RangeGridLayoutHelper",
            "ScrollFixLayoutHelper",
            "SingleLayoutHelper",
            "StaggeredGridLayoutHelper",
            "StickyLayoutHelper"
        )

        fun build(project: Project): Builder {
            val dialog = GenerateItemAdapterDialog(project)
            val screensize = Toolkit.getDefaultToolkit().screenSize
            val x = screensize.getWidth().toInt() / 2 - dialog.preferredSize.width / 2
            val y = screensize.getHeight().toInt() / 2 - dialog.preferredSize.height / 2
            dialog.setLocation(x, y)
            val state = PropertiesComponent.getInstance(project)
            //获取保存的设置
            val usePrefix = state.getBoolean(USE_PREFIX, true)
            val needItemEvent = state.getBoolean(NEED_ITEM_EVENT, true)
            dialog.layoutHelperCb.isEditable = false
            dialog.adapterTypeCb.isEditable = false
            dialog.moduleNameAsPrefixCheckBox.isSelected = usePrefix
            dialog.needItemEventCheckBox.isSelected = needItemEvent
            //添加adapter type选项
            adapterTypeArray.forEach { name ->
                    dialog.adapterTypeCb.addItem(name)
                }
            //添加Layout helpter选项
            layoutHelperArray.sorted()
                .forEach { name ->
                    dialog.layoutHelperCb.addItem(name)
                }
            dialog.adapterTypeCb.addItemListener(object :ItemListener{
                /**
                 * Invoked when an item has been selected or deselected by the user.
                 * The code written for this method performs the operations
                 * that need to occur when an item is selected (or deselected).
                 */
                override fun itemStateChanged(e: ItemEvent) {
                    if (e.stateChange == ItemEvent.SELECTED ) {
                        val selectedItem = dialog.adapterTypeCb.selectedItem as String
                        //dialog.itemEventPanel.isVisible = selectedItem == "ReDataBindingSubAdapter"
                    }
                }

            })


            return Builder(dialog)
        }
    }

    interface OnOkListener {
        fun onOk(config: ItemAdapterConfig)
    }
}

