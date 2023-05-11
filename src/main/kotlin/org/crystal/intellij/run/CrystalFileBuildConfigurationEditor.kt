package org.crystal.intellij.run

import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign

class CrystalFileBuildConfigurationEditor : CrystalFileRunConfigurationEditorBase<CrystalFileBuildConfiguration>() {
    private lateinit var outputFileNameEditor: JBTextField

    override fun Panel.initUI() {
        addCompilerArguments()
        addFileToRun()
        addEnvironmentVariables()
        addOutputFile()
    }

    private fun Panel.addOutputFile() {
        row("Output file name: ") {
            outputFileNameEditor = textField()
                .resizableColumn()
                .horizontalAlign(HorizontalAlign.FILL)
                .component
        }
    }

    override fun resetEditorFrom(configuration: CrystalFileBuildConfiguration) {
        super.resetEditorFrom(configuration)
        outputFileNameEditor.text = configuration.outputFileName
    }

    override fun applyEditorTo(configuration: CrystalFileBuildConfiguration) {
        super.applyEditorTo(configuration)
        configuration.outputFileName = outputFileNameEditor.text
    }
}