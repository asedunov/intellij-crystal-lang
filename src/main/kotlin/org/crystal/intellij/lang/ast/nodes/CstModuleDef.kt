package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstVisitor

class CstModuleDef(
    val name: CstPath,
    val body: CstNode = CstNop,
    val typeVars: List<String> = emptyList(),
    val splatIndex: Int = -1,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstModuleDef

        if (name != other.name) return false
        if (body != other.body) return false
        if (typeVars != other.typeVars) return false
        if (splatIndex != other.splatIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + body.hashCode()
        result = 31 * result + typeVars.hashCode()
        result = 31 * result + splatIndex
        return result
    }

    override fun toString() = buildString {
        append("ModuleDef(")
        append(name)
        if (body != CstNop) append(", body=$body")
        if (typeVars.isNotEmpty()) append(", typeVars=$typeVars")
        if (splatIndex >= 0) append(", splatIndex=$splatIndex")
        append(")")
    }

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitModuleDef(this)

    override fun acceptChildren(visitor: CstVisitor) {
        body.accept(visitor)
    }
}