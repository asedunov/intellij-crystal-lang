package org.crystal.intellij.sdk

import com.intellij.execution.wsl.WSLUtil
import com.intellij.execution.wsl.WslDistributionManager
import com.intellij.openapi.util.SystemInfo
import com.intellij.util.SystemProperties
import java.io.File

private const val STD_COMPILER_PATH = "/usr/bin/crystal"
private const val STD_SHARDS_PATH = "/usr/bin/shards"

sealed interface CrystalToolFinder {
    val isApplicable: Boolean

    fun getCompilers(): Sequence<CrystalToolPeer>
    fun getShardsTools(): Sequence<CrystalToolPeer>
}

object CrystalUnixToolFinder : CrystalToolFinder {
    override val isApplicable: Boolean
        get() = SystemInfo.isUnix

    override fun getCompilers() = sequenceOf(CrystalLocalToolPeer(STD_COMPILER_PATH))

    override fun getShardsTools() = sequenceOf(CrystalLocalToolPeer(STD_SHARDS_PATH))
}

object CrystalWindowsToolFinder : CrystalToolFinder {
    override val isApplicable: Boolean
        get() = SystemInfo.isWindows

    private fun getTools(toolName: String): Sequence<CrystalToolPeer> {
        val userHome = SystemProperties.getUserHome()
        val crystalRoot = File("$userHome\\scoop\\apps\\crystal")
        return crystalRoot.listFiles()?.asSequence()?.mapNotNull {
            CrystalLocalToolPeer(it.absolutePath + "\\$toolName.exe")
        } ?: emptySequence()
    }

    override fun getCompilers() = getTools("crystal")

    override fun getShardsTools() = getTools("shards")
}

object CrystalWslToolFinder : CrystalToolFinder {
    override val isApplicable: Boolean
        get() = WSLUtil.isSystemCompatible()

    private fun getWslTools(path: String): Sequence<CrystalToolPeer> {
        return WslDistributionManager
            .getInstance()
            .installedDistributions
            .asSequence()
            .map {
                CrystalWslToolPeer(path, it)
            }
    }

    override fun getCompilers() = getWslTools(STD_COMPILER_PATH)

    override fun getShardsTools() = getWslTools(STD_SHARDS_PATH)
}

private val finders = listOf(
    CrystalWslToolFinder,
    CrystalUnixToolFinder,
    CrystalWindowsToolFinder
)

fun getCrystalCompilers(): Sequence<CrystalToolPeer> {
    return finders.filter { it.isApplicable }.asSequence().flatMap { it.getCompilers() }
}

fun getCrystalShardsTools(): Sequence<CrystalToolPeer> {
    return finders.filter { it.isApplicable }.asSequence().flatMap { it.getShardsTools() }
}