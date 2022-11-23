package org.crystal.intellij.sdk

import com.intellij.execution.configuration.EnvironmentVariablesData
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.wsl.WSLCommandLineOptions
import com.intellij.execution.wsl.WSLDistribution
import com.intellij.util.PathUtil
import org.crystal.intellij.util.isValidFile
import org.crystal.intellij.util.toPathOrEmpty
import java.io.File
import java.nio.file.Path

sealed class CrystalToolPeer(
    protected val localPath: String
) {
    companion object {
        val EMPTY = CrystalLocalToolPeer("")
    }

    abstract val fullPath: Path

    val isValid: Boolean
        get() = fullPath.isValidFile

    abstract fun convertArgumentPath(path: String): String

    open fun buildCommandLine(
        parameters: List<Any>,
        env: EnvironmentVariablesData? = null
    ): GeneralCommandLine? {
        if (!isValid) return null
        val commandLine = GeneralCommandLine().withExePath(localPath)
        if (env != null) {
            env.configureCommandLine(commandLine, true)
        }
        else {
            commandLine.withEnvironment(System.getenv())
        }
        commandLine.withParameters(parameters.map{
            when (it) {
                is File -> convertArgumentPath(it.absolutePath)
                else -> it.toString()
            }
        })
        return commandLine
    }
}

class CrystalLocalToolPeer(path: String) : CrystalToolPeer(path) {
    override val fullPath: Path by lazy {
        localPath.toPathOrEmpty()
    }

    override fun convertArgumentPath(path: String): String = PathUtil.toSystemDependentName(path)
}

class CrystalWslToolPeer(
    localPath: String,
    private val distribution: WSLDistribution
): CrystalToolPeer(localPath) {
    override val fullPath: Path by lazy {
        distribution.getWindowsPath(localPath).toPathOrEmpty()
    }

    override fun convertArgumentPath(path: String) = distribution.getWslPath(path) ?: ""

    override fun buildCommandLine(
        parameters: List<Any>,
        env: EnvironmentVariablesData?
    ): GeneralCommandLine? {
        return super.buildCommandLine(parameters, env)?.let {
            distribution.patchCommandLine(
                it,
                null,
                WSLCommandLineOptions().setExecuteCommandInShell(false)
            )
        }
    }
}