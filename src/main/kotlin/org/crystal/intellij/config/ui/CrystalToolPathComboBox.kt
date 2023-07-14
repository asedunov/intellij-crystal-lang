/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.crystal.intellij.config.ui

import com.intellij.openapi.application.AppUIExecutor
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.ui.ComboBoxWithWidePopup
import com.intellij.openapi.ui.ComponentWithBrowseButton
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.vfs.StandardFileSystems
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.AnimatedIcon
import com.intellij.ui.ComboboxSpeedSearch
import com.intellij.ui.components.fields.ExtendableTextComponent
import com.intellij.ui.components.fields.ExtendableTextField
import org.crystal.intellij.CrystalBundle
import org.crystal.intellij.sdk.CrystalToolPeer
import org.crystal.intellij.sdk.isValidCompilerPath
import org.crystal.intellij.util.addTextChangeListener
import org.crystal.intellij.util.toPathOrNull
import java.awt.event.ItemEvent
import java.nio.file.Path
import javax.swing.plaf.basic.BasicComboBoxEditor
import kotlin.io.path.name

/**
 * Based on RsToolchainPathChoosingComboBox from IntelliJ Rust plugin (https://github.com/intellij-rust/intellij-rust)
 */
class CrystalToolPathComboBox : ComponentWithBrowseButton<ComboBoxWithWidePopup<Path>>(ComboBoxWithWidePopup(), null) {
    private val editor: BasicComboBoxEditor = object : BasicComboBoxEditor() {
        override fun createEditorComponent(): ExtendableTextField = ExtendableTextField()
    }

    private val pathTextField: ExtendableTextField
        get() = childComponent.editor.editorComponent as ExtendableTextField

    private val busyIconExtension: ExtendableTextComponent.Extension =
        ExtendableTextComponent.Extension { AnimatedIcon.Default.INSTANCE }

    val selectedPathAsText: String
        get() = pathTextField.text

    var selectedPath: Path?
        get() = pathTextField.text.toPathOrNull()
        set(value) {
            pathTextField.text = value?.toString().orEmpty()
        }

    private var enableTextEditorEvents: Boolean = true
    private var isUpdatingPaths: Boolean = false

    init {
        ComboboxSpeedSearch(childComponent)
        childComponent.editor = editor
        childComponent.isEditable = true

        addActionListener {
            @Suppress("DialogTitleCapitalization")
            val descriptor = object : FileChooserDescriptor(
                true,
                false,
                false,
                false,
                false,
                false
            ) {
                @Throws(Exception::class)
                override fun validateSelectedFiles(files: Array<VirtualFile>) {
                    if (files.isEmpty()) return
                    val path = StandardFileSystems.local().getNioPath(files.first()) ?: return
                    if (!path.isValidCompilerPath) {
                        throw Exception(CrystalBundle.message("settings.sdk.invalid.interpreter.name.0", path.name))
                    }
                }
            }.withTitle(CrystalBundle.message("settings.sdk.select.home.path")).withShowHiddenFiles(SystemInfo.isUnix)
            FileChooser.chooseFile(descriptor, null, null) { file ->
                selectedPath = file.toNioPath()
            }
        }
    }

    private fun setBusy(busy: Boolean) {
        if (busy) {
            pathTextField.addExtension(busyIconExtension)
        } else {
            pathTextField.removeExtension(busyIconExtension)
        }
        repaint()
    }

    fun addTextChangeListener(onTextChanged: () -> Unit) {
        pathTextField.addTextChangeListener {
            if (enableTextEditorEvents && !isUpdatingPaths) onTextChanged()
        }
    }

    fun addSelectPathListener(onPathSelected: () -> Unit) {
        childComponent.addItemListener {
            if (!isUpdatingPaths && it.stateChange == ItemEvent.SELECTED && it.item is Path) {
                onPathSelected()
            }
        }
    }

    fun addToolchainsAsync(toolRetriever: () -> List<CrystalToolPeer>) {
        setBusy(true)
        ApplicationManager.getApplication().executeOnPooledThread {
            var tools = emptyList<CrystalToolPeer>()
            try {
                tools = toolRetriever()
            } finally {
                val executor = AppUIExecutor.onUiThread(ModalityState.any()).expireWith(this)
                executor.execute {
                    setBusy(false)
                    val oldSelectedPath = selectedPath
                    isUpdatingPaths = true
                    childComponent.removeAllItems()
                    tools.forEach {
                        childComponent.addItem(it.fullPath)
                    }
                    isUpdatingPaths = false
                    childComponent.selectedItem = null
                    selectedPath = oldSelectedPath
                }
            }
        }
    }
}
