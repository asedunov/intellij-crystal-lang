package org.crystal.intellij.ide.run

import com.intellij.execution.Executor
import com.intellij.execution.configuration.EnvironmentVariablesData
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.LocatableConfigurationBase
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RuntimeConfigurationError
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.util.ProgramParametersConfigurator
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.vfs.StandardFileSystems
import com.intellij.psi.PsiManager
import org.crystal.intellij.ide.shards.yaml.model.findAllShardYamls
import org.crystal.intellij.lang.config.crystalWorkspaceSettings
import org.crystal.intellij.lang.psi.module
import org.crystal.intellij.lang.resolve.crystalPathRoots
import org.crystal.intellij.lang.sdk.CrystalLocalToolPeer
import org.crystal.intellij.util.*
import org.jdom.Element
import java.nio.file.Path
import kotlin.io.path.*

abstract class CrystalFileRunConfigurationBase(
    project: Project,
    name: String,
    factory: ConfigurationFactory
) : LocatableConfigurationBase<RunProfileState>(project, factory, name) {
    var workingDirectory: Path? = runReadAction {
        project.findAllShardYamls().firstOrNull()?.toNioPath()?.parent
    }
    var targetFile: Path? = null
    var env: EnvironmentVariablesData = EnvironmentVariablesData.DEFAULT
    var compilerArguments: String = ""

    abstract val showProgress: Boolean

    abstract val suggestedNamePrefix: String

    override fun suggestedName(): String {
        return suggestedNamePrefix + (targetFile?.fileName?.let { " $it" } ?: "")
    }

    override fun readExternal(element: Element) {
        super.readExternal(element)
        workingDirectory = element.getNestedPath("workingDirectory")
        targetFile = element.getNestedPath("targetFile")
            ?: element.getNestedString("filePath")?.toPathOrNull()
        env = EnvironmentVariablesData.readExternal(element)
        compilerArguments = element.getNestedString("compilerArguments") ?: ""
    }

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        element.addNestedPath("workingDirectory", workingDirectory)
        element.addNestedPath("targetFile", targetFile)
        env.writeExternal(element)
        element.addNestedValue("compilerArguments", compilerArguments)
    }

    override fun checkConfiguration() {
        val compilerTool = project.crystalWorkspaceSettings.compiler
        if (!compilerTool.isValid) throw RuntimeConfigurationError("Crystal is not configured")
        val file = targetFile
        when {
            file == null || !file.exists() -> throw RuntimeConfigurationError("Target file doesn't exist")
            !(file.isRegularFile() && file.extension == "cr") -> throw RuntimeConfigurationError("Target file is not valid")
        }
        val workingDirectory = workingDirectory
        when {
            workingDirectory == null || !workingDirectory.exists() -> throw RuntimeConfigurationError("Working directory doesn't exist")
            !workingDirectory.isDirectory() -> throw RuntimeConfigurationError("Working directory path refers to non-directory")
        }
    }

    abstract val commandArgument: String

    open fun patchArgumentList(arguments: ArrayList<Any>) {}

    override fun getState(executor: Executor, environment: ExecutionEnvironment): CrystalFileRunState? {
        val targetFile = targetFile ?: return null
        val workingDirectory = workingDirectory ?: return null
        val parameters = ArrayList<Any>(4)

        parameters += commandArgument

        if (showProgress) {
            parameters += "--progress"
        }

        val compilerArgList = ProgramParametersConfigurator.expandMacrosAndParseParameters(compilerArguments)
        parameters.addAll(compilerArgList)

        parameters += targetFile.toFile()

        patchArgumentList(parameters)

        val compiler = project.crystalWorkspaceSettings.compiler
        if (!compiler.isValid) return null

        var effectiveEnv = env
        val project = environment.project
        val module = StandardFileSystems
            .local()
            .findFileByPath(targetFile.pathString)
            ?.toPsi(PsiManager.getInstance(project))
            ?.module()
        val crystalPathRoots = module?.crystalPathRoots() ?: emptyList()
        if (crystalPathRoots.isNotEmpty()) {
            val separator = if (compiler.peer is CrystalLocalToolPeer && SystemInfo.isWindows) ";" else ":"
            val crystalPath = crystalPathRoots.joinToString(separator) {
                compiler.peer.convertArgumentPath(it.virtualFile.path)
            }
            val envMap = HashMap(env.envs)
            envMap["CRYSTAL_PATH"] = crystalPath
            effectiveEnv = env.with(envMap)
        }

        val commandLine = compiler.peer
            .buildCommandLine(parameters, effectiveEnv)
            ?.withWorkDirectory(workingDirectory.toFile()) ?: return null
        return CrystalFileRunState(commandLine, environment)
    }
}