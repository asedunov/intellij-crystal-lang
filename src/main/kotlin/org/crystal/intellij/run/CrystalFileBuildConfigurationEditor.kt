package org.crystal.intellij.run

import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.LayoutBuilder

class CrystalFileBuildConfigurationEditor : CrystalFileRunConfigurationEditorBase<CrystalFileBuildConfiguration>() {
    private lateinit var outputFileNameEditor: JBTextField

    override fun LayoutBuilder.initUI() {
        addCompilerArguments()
        addFileToRun()
        addEnvironmentVariables()
        addWorkingDirectory()
        addOutputFile()
    }

    private fun LayoutBuilder.addOutputFile() {
        row("Output file name: ") {
            outputFileNameEditor = JBTextField()(growX).component
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