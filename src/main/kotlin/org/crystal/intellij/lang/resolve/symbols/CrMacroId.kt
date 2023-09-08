package org.crystal.intellij.lang.resolve.symbols

import com.intellij.util.ConcurrencyUtil
import com.intellij.util.containers.ContainerUtil

sealed interface CrMacroId {
    val name: String
}

@JvmInline
value class CrMacroName(override val name: String): CrMacroId

data class CrMacroSignature(
    override val name: String,
    private val paramCount: Int = 0,
    private val splatIndex: Int = -1,
    val hasDoubleSplat: Boolean = false,
    private val requiredExternalNames: List<String> = emptyList()
) : CrMacroId {
    companion object {
        private val signatureInterner = ContainerUtil.createConcurrentWeakKeyWeakValueMap<CrMacroSignature, CrMacroSignature>()
    }

    fun intern(): CrMacroSignature = ConcurrencyUtil.cacheOrGet(signatureInterner, this, this)

    fun serialize(): String {
        return "$name($paramCount|$splatIndex|${if (hasDoubleSplat) 1 else 0}|${requiredExternalNames.joinToString()})"
    }
}