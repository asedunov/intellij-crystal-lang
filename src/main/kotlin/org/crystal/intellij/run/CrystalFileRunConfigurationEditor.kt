package org.crystal.intellij.run

import com.intellij.ui.RawCommandLineEditor
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.layout.LayoutBuilder

class CrystalFileRunConfigurationEditor : CrystalFileRunConfigurationEditorBase<CrystalFileRunConfiguration>() {
    private lateinit var showProgressEditor: JBCheckBox
    private lateinit var programArgumentsEditor: RawCommandLineEditor

    override fun LayoutBuilder.initUI() {
        addProgress()
        addCompilerArguments()
        addFileToRun()
        addProgramArguments()
        addEnvironmentVariables()
    }

    private fun LayoutBuilder.addProgramArguments() {
        row("Program arguments: ") {
            programArgumentsEditor = RawCommandLineEditor()(growX).component
        }
    }

    private fun LayoutBuilder.addProgress() {
        row {
            showProgressEditor = checkBox("Show compilation progress").component
        }
    }

    override fun resetEditorFrom(configuration: CrystalFileRunConfiguration) {
        super.resetEditorFrom(configuration)
        showProgressEditor.isSelected = configuration.showProgress
        programArgumentsEditor.text = configuration.programArguments
    }

    override fun applyEditorTo(configuration: CrystalFileRunConfiguration) {
        super.applyEditorTo(configuration)
        configuration.showProgress = showProgressEditor.isSelected
        configuration.programArguments = programArgumentsEditor.text
    }
}