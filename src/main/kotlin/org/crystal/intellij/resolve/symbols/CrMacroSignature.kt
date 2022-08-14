package org.crystal.intellij.resolve.symbols

import com.intellij.util.ConcurrencyUtil
import com.intellij.util.containers.ContainerUtil

data class CrMacroSignature(
    val name: String,
    private val paramCount: Int = 0,
    private val splatIndex: Int = -1,
    val hasDoubleSplat: Boolean = false,
    private val requiredExternalNames: List<String> = emptyList()
) {
    companion object {
        private val signatureInterner = ContainerUtil.createConcurrentWeakKeyWeakValueMap<CrMacroSignature, CrMacroSignature>()
    }

    fun intern(): CrMacroSignature = ConcurrencyUtil.cacheOrGet(signatureInterner, this, this)

    fun serialize(): String {
        return "$name($paramCount|$splatIndex|${if (hasDoubleSplat) 1 else 0}|${requiredExternalNames.joinToString()})"
    }
}