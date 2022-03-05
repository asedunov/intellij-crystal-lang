package org.crystal.intellij.sdk

import com.intellij.openapi.util.SystemInfo
import com.intellij.util.containers.sequenceOfNotNull
import org.crystal.intellij.util.toPathOrNull

class CrystalUnixSdkFlavor : CrystalSdkFlavor() {
    override val isApplicable: Boolean
        get() = SystemInfo.isUnix

    override fun suggestCrystalExeCandidates() = sequenceOfNotNull("/usr/bin/crystal".toPathOrNull())
}