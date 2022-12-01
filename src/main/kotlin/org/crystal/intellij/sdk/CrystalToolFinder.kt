package org.crystal.intellij.sdk

import com.intellij.execution.wsl.WSLUtil
import com.intellij.execution.wsl.WslDistributionManager
import com.intellij.openapi.util.SystemInfo

private const val STD_COMPILER_PATH = "/usr/bin/crystal"

sealed interface CrystalToolFinder {
    val isApplicable: Boolean

    fun getCompilers(): Sequence<CrystalTool>
}

object CrystalUnixToolFinder : CrystalToolFinder {
    override val isApplicable: Boolean
        get() = SystemInfo.isUnix

    override fun getCompilers() = sequenceOf(CrystalLocalTool(STD_COMPILER_PATH))
}

object CrystalWslToolFinder : CrystalToolFinder {
    override val isApplicable: Boolean
        get() = WSLUtil.isSystemCompatible()

    override fun getCompilers(): Sequence<CrystalTool> {
        return WslDistributionManager
            .getInstance()
            .installedDistributions
            .asSequence()
            .map {
                CrystalWslTool(STD_COMPILER_PATH, it)
            }
    }
}

private val finders = listOf(
    CrystalWslToolFinder,
    CrystalUnixToolFinder
)

fun getCrystalCompilers(): Sequence<CrystalTool> {
    return finders.filter { it.isApplicable }.asSequence().flatMap { it.getCompilers() }
}