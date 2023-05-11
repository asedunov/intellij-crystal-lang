package org.crystal.intellij.run

import com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.vfs.StandardFileSystems
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.RawCommandLineEditor
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.util.io.exists
import com.intellij.util.io.isFile
import org.crystal.intellij.CrystalBundle
import javax.swing.JComponent
import kotlin.io.path.extension
import kotlin.io.path.name

abstract class CrystalFileRunConfigurationEditorBase<C : CrystalFileRunConfigurationBase> : SettingsEditor<C>() {
    private lateinit var fileEditor: TextFieldWithBrowseButton
    private lateinit var envEditor: EnvironmentVariablesTextFieldWithBrowseButton
    private lateinit var compilerArgumentsEditor: RawCommandLineEditor

    private val component by lazy {
        panel {
            initUI()
        }
    }

    protected fun Panel.addEnvironmentVariables() {
        row("Environment variables: ") {
            envEditor = cell(EnvironmentVariablesTextFieldWithBrowseButton())
                .resizableColumn()
                .horizontalAlign(HorizontalAlign.FILL)
                .component
        }
    }

    protected fun Panel.addFileToRun() {
        row("Target file: ") {
            fileEditor = textFieldWithBrowseButton(null, null, fileChooserDescriptor())
                .resizableColumn()
                .horizontalAlign(HorizontalAlign.FILL)
                .component
        }
    }

    protected fun Panel.addCompilerArguments() {
        row("Compiler arguments: ") {
            compilerArgumentsEditor = cell(RawCommandLineEditor())
                .resizableColumn()
                .horizontalAlign(HorizontalAlign.FILL)
                .component
        }
    }

    protected abstract fun Panel.initUI()

    private fun fileChooserDescriptor(): FileChooserDescriptor {
        return object : FileChooserDescriptor(
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
                if (!(path.exists() && path.isFile() && path.extension == "cr")) {
                    throw Exception(CrystalBundle.message("settings.sdk.invalid.interpreter.name.0", path.name))
                }
            }
        }.withTitle(CrystalBundle.message("run.select.file"))
    }

    override fun createEditor(): JComponent = component

    override fun resetEditorFrom(configuration: C) {
        fileEditor.text = configuration.filePath ?: ""
        envEditor.data = configuration.env
        compilerArgumentsEditor.text = configuration.compilerArguments
    }

    override fun applyEditorTo(configuration: C) {
        configuration.filePath = fileEditor.text
        configuration.env = envEditor.data
        configuration.compilerArguments = compilerArgumentsEditor.text
    }
}