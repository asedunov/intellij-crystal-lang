package org.crystal.intellij.sdk

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.util.SystemInfo
import com.intellij.util.containers.sequenceOfNotNull
import org.crystal.intellij.util.toPathOrNull
import java.nio.file.Path

class CrystalUnixSdkFlavor : CrystalSdkFlavor() {
    override val isApplicable: Boolean
        get() = SystemInfo.isUnix

    override fun suggestCrystalExeCandidates() = sequenceOfNotNull("/usr/bin/crystal".toPathOrNull())

    override fun crystalCommandLine(crystalExePath: Path, parameters: List<String>): GeneralCommandLine {
        return GeneralCommandLine()
            .withExePath(crystalExePath.toString())
            .withParameters(parameters)
            .withEnvironment(System.getenv())
    }
}