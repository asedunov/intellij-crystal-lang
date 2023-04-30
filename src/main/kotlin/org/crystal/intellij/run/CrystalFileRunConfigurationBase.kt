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
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.vfs.StandardFileSystems
import com.intellij.psi.PsiManager
import com.intellij.util.io.isDirectory
import org.crystal.intellij.config.crystalWorkspaceSettings
import org.crystal.intellij.psi.module
import org.crystal.intellij.resolve.crystalPathRoots
import org.crystal.intellij.sdk.CrystalLocalToolPeer
import org.crystal.intellij.shards.yaml.model.findAllShardYamls
import org.crystal.intellij.util.*
import org.jdom.Element
import java.io.File
import java.nio.file.Path
import kotlin.io.path.exists

abstract class CrystalFileRunConfigurationBase(
    project: Project,
    name: String,
    factory: ConfigurationFactory
) : LocatableConfigurationBase<RunProfileState>(project, factory, name) {
    var workingDirectory: Path? = project.findAllShardYamls().firstOrNull()?.toNioPath()?.parent
    var filePath: String? = null
    var env: EnvironmentVariablesData = EnvironmentVariablesData.DEFAULT
    var compilerArguments: String = ""

    abstract val showProgress: Boolean

    abstract val suggestedNamePrefix: String

    override fun suggestedName(): String {
        return suggestedNamePrefix + (filePath?.toPathOrNull()?.fileName?.let { " $it" } ?: "")
    }

    override fun readExternal(element: Element) {
        super.readExternal(element)
        workingDirectory = element.getNestedPath("workingDirectory")
        filePath = element.getNestedString("filePath")
        env = EnvironmentVariablesData.readExternal(element)
        compilerArguments = element.getNestedString("compilerArguments") ?: ""
    }

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        element.addNestedPath("workingDirectory", workingDirectory)
        element.addNestedValue("filePath", filePath)
        env.writeExternal(element)
        element.addNestedValue("compilerArguments", compilerArguments)
    }

    override fun checkConfiguration() {
        val compilerTool = project.crystalWorkspaceSettings.compiler
        if (!compilerTool.isValid) throw RuntimeConfigurationError("Crystal is not configured")
        val file = filePath?.let(::File)
        when {
            file == null || !file.exists() -> throw RuntimeConfigurationError("Target file doesn't exist")
            !(file.isFile && file.extension == "cr") -> throw RuntimeConfigurationError("Target file is not valid")
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
        val filePath = filePath ?: return null
        val workingDirectory = workingDirectory ?: return null
        val parameters = ArrayList<Any>(4)

        parameters += commandArgument

        if (showProgress) {
            parameters += "--progress"
        }

        val compilerArgList = ProgramParametersConfigurator.expandMacrosAndParseParameters(compilerArguments)
        parameters.addAll(compilerArgList)

        parameters += File(filePath)

        patchArgumentList(parameters)

        val compiler = project.crystalWorkspaceSettings.compiler
        if (!compiler.isValid) return null

        var effectiveEnv = env
        val project = environment.project
        val module = StandardFileSystems
            .local()
            .findFileByPath(filePath)
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