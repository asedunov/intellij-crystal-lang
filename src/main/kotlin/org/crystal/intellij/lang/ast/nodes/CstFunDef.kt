package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstFunDef(
    val name: String,
    val args: List<CstArg> = emptyList(),
    val returnType: CstNode? = null,
    val body: CstNode? = null,
    val realName: String = name,
    val isVariadic: Boolean = false,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstFunDef

        if (name != other.name) return false
        if (args != other.args) return false
        if (returnType != other.returnType) return false
        if (body != other.body) return false
        if (realName != other.realName) return false
        if (isVariadic != other.isVariadic) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + args.hashCode()
        result = 31 * result + (returnType?.hashCode() ?: 0)
        result = 31 * result + (body?.hashCode() ?: 0)
        result = 31 * result + realName.hashCode()
        result = 31 * result + isVariadic.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("FunDef(")
        append(name)
        if (args.isNotEmpty()) append(", args=$args")
        if (returnType != null) append(", returnType=$returnType")
        if (body != null) append(", body=$body")
        if (realName != name) append(", realName=$realName")
        if (isVariadic) append(", isVariadic")
        append(")")
    }
}