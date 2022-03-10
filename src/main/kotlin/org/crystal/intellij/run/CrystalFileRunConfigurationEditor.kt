package org.crystal.intellij.run

import com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.vfs.StandardFileSystems
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.RawCommandLineEditor
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.layout.panel
import com.intellij.util.io.exists
import com.intellij.util.io.isFile
import org.crystal.intellij.CrystalBundle
import javax.swing.JComponent
import kotlin.io.path.extension
import kotlin.io.path.name

@Suppress("UnstableApiUsage")
class CrystalFileRunConfigurationEditor : SettingsEditor<CrystalFileRunConfiguration>() {
    private lateinit var fileEditor: TextFieldWithBrowseButton
    private lateinit var showProgressEditor: JBCheckBox
    private lateinit var envEditor: EnvironmentVariablesTextFieldWithBrowseButton
    private lateinit var compilerArgumentsEditor: RawCommandLineEditor
    private lateinit var programArgumentsEditor: RawCommandLineEditor

    private val component by lazy {
        panel {
            row {
                showProgressEditor = checkBox("Show compilation progress").component
            }
            row("Compiler arguments: ") {
                compilerArgumentsEditor = RawCommandLineEditor()(growX).component
            }
            row("Target file: ") {
                fileEditor = textFieldWithBrowseButton(null, "", null, fileChooserDescriptor()).component
            }
            row("Program arguments: ") {
                programArgumentsEditor = RawCommandLineEditor()(growX).component
            }
            row("Environment variables: ") {
                envEditor = EnvironmentVariablesTextFieldWithBrowseButton()(growX).component
            }
        }
    }

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

    override fun resetEditorFrom(configuration: CrystalFileRunConfiguration) {
        fileEditor.text = configuration.filePath ?: ""
        showProgressEditor.isSelected = configuration.showProgress
        envEditor.data = configuration.env
        compilerArgumentsEditor.text = configuration.compilerArguments
        programArgumentsEditor.text = configuration.programArguments
    }

    override fun applyEditorTo(configuration: CrystalFileRunConfiguration) {
        configuration.filePath = fileEditor.text
        configuration.showProgress = showProgressEditor.isSelected
        configuration.env = envEditor.data
        configuration.compilerArguments = compilerArgumentsEditor.text
        configuration.programArguments = programArgumentsEditor.text
    }
}