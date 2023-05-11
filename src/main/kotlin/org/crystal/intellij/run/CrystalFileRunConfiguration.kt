package org.crystal.intellij.run

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.util.ProgramParametersConfigurator
import com.intellij.openapi.project.Project
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
        showProgress = element.getChild("showProgress")?.text.toBoolean()
        programArguments = element.getChild("programArguments")?.text ?: ""
    }

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        element.addContent(Element("showProgress").apply { text = "$showProgress" })
        element.addContent(Element("programArguments").apply { text = programArguments })
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