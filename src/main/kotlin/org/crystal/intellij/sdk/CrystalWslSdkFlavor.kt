package org.crystal.intellij.sdk

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.wsl.WSLCommandLineOptions
import com.intellij.execution.wsl.WSLUtil
import com.intellij.execution.wsl.WslDistributionManager
import com.intellij.execution.wsl.WslPath
import org.crystal.intellij.util.toPathOrNull
import java.nio.file.Path

class CrystalWslSdkFlavor : CrystalSdkFlavor() {
    override val isApplicable: Boolean
        get() = WSLUtil.isSystemCompatible()

    override fun suggestCrystalExeCandidates() =
        WslDistributionManager.getInstance()
            .installedDistributions
            .asSequence()
            .mapNotNull { it.getWindowsPath("/usr/bin/crystal").toPathOrNull() }

    override fun crystalCommandLine(crystalExePath: Path, parameters: List<String>): GeneralCommandLine? {
        val wslPath = WslPath.parseWindowsUncPath(crystalExePath.toString()) ?: return null
        return GeneralCommandLine()
            .withExePath(wslPath.linuxPath)
            .withParameters(parameters)
            .withEnvironment(System.getenv())
            .let { wslPath.distribution.patchCommandLine(it, null, commandLineOptions()) }
    }

    private fun commandLineOptions() = WSLCommandLineOptions().setExecuteCommandInShell(false)
}