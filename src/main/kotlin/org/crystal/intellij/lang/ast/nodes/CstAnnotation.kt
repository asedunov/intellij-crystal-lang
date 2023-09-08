package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

class CstAnnotation(
    val path: CstPath,
    val args: List<CstNode> = emptyList(),
    val namedArgs: List<CstNamedArgument> = emptyList(),
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstAnnotation

        if (path != other.path) return false
        if (args != other.args) return false
        if (namedArgs != other.namedArgs) return false

        return true
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + args.hashCode()
        result = 31 * result + namedArgs.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("Annotation(")
        append(path)
        if (args.isNotEmpty()) append(", args=$args")
        if (namedArgs.isNotEmpty()) append(", namedArgs=$namedArgs")
        append(")")
    }
}