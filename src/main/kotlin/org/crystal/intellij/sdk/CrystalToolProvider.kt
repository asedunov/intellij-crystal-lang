package org.crystal.intellij.sdk

import com.intellij.execution.wsl.WslPath

sealed interface CrystalToolProvider {
    fun getTool(path: String): CrystalToolPeer?
}

object CrystalLocalToolProvider : CrystalToolProvider {
    override fun getTool(path: String) = CrystalLocalToolPeer(path)
}

object CrystalWslToolProvider : CrystalToolProvider {
    override fun getTool(path: String): CrystalToolPeer? {
        val wslPath = WslPath.parseWindowsUncPath(path) ?: return null
        return CrystalWslToolPeer(wslPath.linuxPath, wslPath.distribution)
    }
}

private val providers = listOf(
    CrystalWslToolProvider,
    CrystalLocalToolProvider
)

fun getCrystalTool(path: String): CrystalToolPeer? {
    return providers.firstNotNullOfOrNull { it.getTool(path) }
}

fun getCrystalCompiler(path: String): CrystalCompiler {
    return CrystalCompiler(getCrystalTool(path) ?: CrystalToolPeer.EMPTY)
}