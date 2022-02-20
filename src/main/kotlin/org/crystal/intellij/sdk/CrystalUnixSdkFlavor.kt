package org.crystal.intellij.sdk

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.util.SystemInfo
import org.crystal.intellij.util.toPathOrNull
import java.nio.file.Path

class CrystalUnixSdkFlavor : CrystalSdkFlavor() {
    override val isApplicable: Boolean
        get() = SystemInfo.isUnix

    override fun suggestCrystalExeCandidates(): Sequence<Path> {
        val path = "/usr/bin/crystal".toPathOrNull() ?: return emptySequence()
        return sequenceOf(path)
    }

    override fun crystalCommandLine(crystalExePath: Path, parameters: List<String>): GeneralCommandLine {
        return GeneralCommandLine()
            .withExePath(crystalExePath.toString())
            .withParameters(parameters)
            .withEnvironment(System.getenv())
    }
}