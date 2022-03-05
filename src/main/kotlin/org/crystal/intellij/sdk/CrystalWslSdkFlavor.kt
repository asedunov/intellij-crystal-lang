package org.crystal.intellij.sdk

import com.intellij.execution.configuration.EnvironmentVariablesData
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.wsl.WSLCommandLineOptions
import com.intellij.execution.wsl.WSLUtil
import com.intellij.execution.wsl.WslDistributionManager
import com.intellij.execution.wsl.WslPath
import org.crystal.intellij.util.toPathOrNull

class CrystalWslSdkFlavor : CrystalSdkFlavor() {
    override val isApplicable: Boolean
        get() = WSLUtil.isSystemCompatible()

    override fun suggestCrystalExeCandidates() =
        WslDistributionManager.getInstance()
            .installedDistributions
            .asSequence()
            .mapNotNull { it.getWindowsPath("/usr/bin/crystal")?.toPathOrNull() }

    override fun createCrystalExe(crystalExePath: String): CrystalExe {
        val wslPath = WslPath.parseWindowsUncPath(crystalExePath) ?: return super.createCrystalExe(crystalExePath)
        return object : CrystalExe(crystalExePath) {
            override fun crystalCommandLine(
                parameters: List<Any>,
                env: EnvironmentVariablesData?
            ): GeneralCommandLine? {
                return super.crystalCommandLine(parameters, env)?.let {
                    wslPath.distribution.patchCommandLine(
                        it,
                        null,
                        WSLCommandLineOptions().setExecuteCommandInShell(false)
                    )
                }
            }

            override fun initCommandLine(): GeneralCommandLine {
                return GeneralCommandLine().withExePath(wslPath.linuxPath)
            }

            override fun convertPathForCrystal(path: String): String {
                return wslPath.distribution.getWslPath(path) ?: ""
            }
        }
    }

}