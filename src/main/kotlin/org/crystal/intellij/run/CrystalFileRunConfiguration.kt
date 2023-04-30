package org.crystal.intellij.run

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.util.ProgramParametersConfigurator
import com.intellij.openapi.project.Project
import org.crystal.intellij.util.addNestedValue
import org.crystal.intellij.util.getNestedBoolean
import org.crystal.intellij.util.getNestedString
import org.jdom.Element

class CrystalFileRunConfiguration(
    project: Project,
    name: String,
    factory: ConfigurationFactory
) : CrystalFileRunConfigurationBase(project, name, factory) {
    override var showProgress: Boolean = true
    var programArguments: String = ""

    override val suggestedNamePrefix: String
        get() = "Build and run"

    override fun readExternal(element: Element) {
        super.readExternal(element)
        showProgress = element.getNestedBoolean("showProgress")
        programArguments = element.getNestedString("programArguments") ?: ""
    }

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        element.addNestedValue("showProgress", showProgress)
        element.addNestedValue("programArguments", programArguments)
    }

    override val commandArgument: String
        get() = "run"

    override fun patchArgumentList(arguments: ArrayList<Any>) {
        val programArgList = ProgramParametersConfigurator.expandMacrosAndParseParameters(programArguments)
        if (programArgList.isNotEmpty()) {
            arguments += "--"
            arguments.addAll(programArgList)
        }
    }

    override fun getConfigurationEditor() = CrystalFileRunConfigurationEditor()
}