package org.crystal.intellij.run

import com.intellij.ui.RawCommandLineEditor
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign

class CrystalFileRunConfigurationEditor : CrystalFileRunConfigurationEditorBase<CrystalFileRunConfiguration>() {
    private lateinit var showProgressEditor: JBCheckBox
    private lateinit var programArgumentsEditor: RawCommandLineEditor

    override fun Panel.initUI() {
        addProgress()
        addCompilerArguments()
        addFileToRun()
        addProgramArguments()
        addEnvironmentVariables()
    }

    private fun Panel.addProgramArguments() {
        row("Program arguments: ") {
            programArgumentsEditor = cell(RawCommandLineEditor())
                .resizableColumn()
                .horizontalAlign(HorizontalAlign.FILL)
                .component
        }
    }

    private fun Panel.addProgress() {
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