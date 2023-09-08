package org.crystal.intellij.ide.run

import com.intellij.execution.ExecutionBundle
import com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.vfs.StandardFileSystems
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.RawCommandLineEditor
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.io.exists
import com.intellij.util.io.isFile
import org.crystal.intellij.CrystalBundle
import org.crystal.intellij.util.textAsPath
import java.nio.file.Path
import javax.swing.JComponent
import kotlin.io.path.extension
import kotlin.io.path.name

abstract class CrystalFileRunConfigurationEditorBase<C : CrystalFileRunConfigurationBase> : SettingsEditor<C>() {
    private lateinit var workingDirectoryEditor: TextFieldWithBrowseButton
    private lateinit var targetFileEditor: TextFieldWithBrowseButton
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
                .align(AlignX.FILL)
                .component
        }
    }

    protected fun Panel.addWorkingDirectory() {
        row(ExecutionBundle.message("run.configuration.working.directory.label")) {
            workingDirectoryEditor = textFieldWithBrowseButton()
                .resizableColumn()
                .align(AlignX.FILL)
                .component
                .apply {
                    val fileChooser = FileChooserDescriptorFactory.createSingleFolderDescriptor().apply {
                        title = ExecutionBundle.message("select.working.directory.message")
                    }
                    addBrowseFolderListener(null, null, null, fileChooser)
                }
        }
    }

    protected fun Panel.addFileToRun() {
        row("Target file: ") {
            targetFileEditor = textFieldWithBrowseButton(null, null, fileChooserDescriptor())
                .resizableColumn()
                .align(AlignX.FILL)
                .component
        }
    }

    protected fun Panel.addCompilerArguments() {
        row("Compiler arguments: ") {
            compilerArgumentsEditor = cell(RawCommandLineEditor())
                .resizableColumn()
                .align(AlignX.FILL)
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

    private var targetFile: Path? by textAsPath(::targetFileEditor)
    private var workingDirectory: Path? by textAsPath(::workingDirectoryEditor)

    override fun resetEditorFrom(configuration: C) {
        targetFile = configuration.targetFile
        envEditor.data = configuration.env
        compilerArgumentsEditor.text = configuration.compilerArguments
        workingDirectory = configuration.workingDirectory
    }

    override fun applyEditorTo(configuration: C) {
        configuration.targetFile = targetFile
        configuration.env = envEditor.data
        configuration.compilerArguments = compilerArgumentsEditor.text
        configuration.workingDirectory = workingDirectory
    }
}