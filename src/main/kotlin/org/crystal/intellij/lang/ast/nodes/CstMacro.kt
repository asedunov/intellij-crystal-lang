package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstMacro(
    val name: String,
    val args: List<CstArg> = emptyList(),
    val body: CstNode = CstNop,
    val doubleSplat: CstArg? = null,
    val blockArg: CstArg? = null,
    val splatIndex: Int = -1,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstMacro

        if (name != other.name) return false
        if (args != other.args) return false
        if (body != other.body) return false
        if (doubleSplat != other.doubleSplat) return false
        if (blockArg != other.blockArg) return false
        if (splatIndex != other.splatIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + args.hashCode()
        result = 31 * result + body.hashCode()
        result = 31 * result + (doubleSplat?.hashCode() ?: 0)
        result = 31 * result + (blockArg?.hashCode() ?: 0)
        result = 31 * result + splatIndex
        return result
    }

    override fun toString() = buildString {
        append("Macro(")
        append(name)
        if (args.isNotEmpty()) append(", args=$args")
        if (body != CstNop) append(", body=$body")
        if (blockArg != null) append(", blockArg=$blockArg")
        if (splatIndex >= 0) append(", splatIndex=$splatIndex")
        if (doubleSplat != null) append(", doubleSplat=$doubleSplat")
        append(")")
    }
}