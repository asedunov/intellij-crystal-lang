package org.crystal.intellij.sdk

import com.intellij.execution.wsl.WslPath

sealed interface CrystalToolProvider {
    fun getTool(path: String): CrystalTool?
}

object CrystalLocalToolProvider : CrystalToolProvider {
    override fun getTool(path: String) = CrystalLocalTool(path)
}

object CrystalWslToolProvider : CrystalToolProvider {
    override fun getTool(path: String): CrystalTool? {
        val wslPath = WslPath.parseWindowsUncPath(path) ?: return null
        return CrystalWslTool(wslPath.linuxPath, wslPath.distribution)
    }
}

private val providers = listOf(
    CrystalWslToolProvider,
    CrystalLocalToolProvider
)

fun getCrystalTool(path: String): CrystalTool? {
    return providers.firstNotNullOfOrNull { it.getTool(path) }
}