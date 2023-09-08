package org.crystal.intellij.ide.run

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.project.Project
import org.crystal.intellij.util.addNestedValue
import org.crystal.intellij.util.getNestedString
import org.jdom.Element

class CrystalFileBuildConfiguration(
    project: Project,
    name: String,
    factory: ConfigurationFactory
) : CrystalFileRunConfigurationBase(project, name, factory) {
    var outputFileName: String = ""

    override val showProgress: Boolean
        get() = true

    override val suggestedNamePrefix: String
        get() = "Build"

    override val commandArgument: String
        get() = "build"

    override fun readExternal(element: Element) {
        super.readExternal(element)
        outputFileName = element.getNestedString("outputFileName") ?: ""
    }

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        element.addNestedValue("outputFileName", outputFileName)
    }

    override fun patchArgumentList(arguments: ArrayList<Any>) {
        if (outputFileName.isNotBlank()) {
            arguments += "-o"
            arguments += outputFileName
        }
    }

    override fun getConfigurationEditor() = CrystalFileBuildConfigurationEditor()
}