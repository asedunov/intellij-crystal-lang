package org.crystal.intellij.sdk

import com.intellij.execution.configuration.EnvironmentVariablesData
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.util.PathUtil
import org.crystal.intellij.util.toPathOrNull
import java.io.File

open class CrystalExe(crystalExePath: String) {
    companion object {
        val EMPTY = CrystalExe("")
    }

    private val crystalExePath = crystalExePath.toPathOrNull()

    val isValid: Boolean
        get() = crystalExePath != null && CrystalSdkFlavor.isValidCrystalExePath(crystalExePath)

    open fun convertPathForCrystal(path: String): String = PathUtil.toSystemDependentName(path)

    open fun crystalCommandLine(
        parameters: List<Any>,
        env: EnvironmentVariablesData? = null
    ): GeneralCommandLine? {
        if (crystalExePath == null || !CrystalSdkFlavor.isValidCrystalExePath(crystalExePath)) return null
        val commandLine = initCommandLine()
        if (env != null) {
            env.configureCommandLine(commandLine, true)
        }
        else {
            commandLine.withEnvironment(System.getenv())
        }
        commandLine.withParameters(parameters.map{
            when (it) {
                is File -> convertPathForCrystal(it.absolutePath)
                else -> it.toString()
            }
        })
        return commandLine
    }

    protected open fun initCommandLine(): GeneralCommandLine {
        return GeneralCommandLine().withExePath(crystalExePath?.toString() ?: "")
    }
}