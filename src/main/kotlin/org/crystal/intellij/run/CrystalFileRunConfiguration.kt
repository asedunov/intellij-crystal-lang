package org.crystal.intellij.run

import com.intellij.execution.Executor
import com.intellij.execution.configuration.EnvironmentVariablesData
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RuntimeConfigurationError
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.util.ProgramParametersConfigurator
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.StandardFileSystems
import com.intellij.psi.PsiManager
import org.crystal.intellij.config.crystalWorkspaceSettings
import org.crystal.intellij.psi.module
import org.crystal.intellij.resolve.crystalPathRoots
import org.crystal.intellij.util.toPathOrNull
import org.crystal.intellij.util.toPsi
import org.jdom.Element
import java.io.File

class CrystalFileRunConfiguration(
    project: Project,
    name: String,
    factory: ConfigurationFactory
) : LocatableConfigurationBase<RunProfileState>(project, factory, name) {
    var filePath: String? = null
    var showProgress: Boolean = true
    var env: EnvironmentVariablesData = EnvironmentVariablesData.DEFAULT
    var compilerArguments: String = ""
    var programArguments: String = ""

    override fun suggestedName(): String {
        return "Build and run" + (filePath?.toPathOrNull()?.fileName?.let { " $it" } ?: "")
    }

    override fun readExternal(element: Element) {
        super.readExternal(element)
        filePath = element.getChild("filePath")?.text
        showProgress = element.getChild("showProgress")?.text.toBoolean()
        env = EnvironmentVariablesData.readExternal(element)
        compilerArguments = element.getChild("compilerArguments")?.text ?: ""
        programArguments = element.getChild("programArguments")?.text ?: ""
    }

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        element.addContent(Element("filePath").apply { text = filePath })
        element.addContent(Element("showProgress").apply { text = "$showProgress" })
        env.writeExternal(element)
        element.addContent(Element("compilerArguments").apply { text = compilerArguments })
        element.addContent(Element("programArguments").apply { text = programArguments })
    }

    override fun checkConfiguration() {
        val compilerTool = project.crystalWorkspaceSettings.compilerTool
        if (!compilerTool.isValid) throw RuntimeConfigurationError("Crystal is not configured")
        val file = filePath?.let(::File)
        when {
            file == null || !file.exists() -> throw RuntimeConfigurationError("Target file doesn't exist")
            !(file.isFile && file.extension == "cr") -> throw RuntimeConfigurationError("Target file is not valid")
        }
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): CrystalFileRunState? {
        val filePath = filePath ?: return null
        val parameters = ArrayList<Any>(4)

        parameters += "run"

        if (showProgress) {
            parameters += "--progress"
        }

        val compilerArgList = ProgramParametersConfigurator.expandMacrosAndParseParameters(compilerArguments)
        parameters.addAll(compilerArgList)

        parameters += File(filePath)

        val programArgList = ProgramParametersConfigurator.expandMacrosAndParseParameters(programArguments)
        if (programArgList.isNotEmpty()) {
            parameters += "--"
            parameters.addAll(programArgList)
        }

        val compilerTool = project.crystalWorkspaceSettings.compilerTool
        if (!compilerTool.isValid) return null

        var effectiveEnv = env
        val project = environment.project
        val module = StandardFileSystems
            .local()
            .findFileByPath(filePath)
            ?.toPsi(PsiManager.getInstance(project))
            ?.module()
        val crystalPathRoots = module?.crystalPathRoots() ?: emptyList()
        if (crystalPathRoots.isNotEmpty()) {
            val crystalPath = crystalPathRoots.joinToString(":") {
                compilerTool.convertArgumentPath(it.virtualFile.path)
            }
            val envMap = HashMap(env.envs)
            envMap["CRYSTAL_PATH"] = crystalPath
            effectiveEnv = env.with(envMap)
        }

        val commandLine = compilerTool.buildCommandLine(parameters, effectiveEnv) ?: return null
        return CrystalFileRunState(commandLine, environment)
    }

    override fun getConfigurationEditor() = CrystalFileRunConfigurationEditor()
}