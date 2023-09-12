package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstVisitor

class CstProcNotation(
    val inputs: List<CstNode> = emptyList(),
    val output: CstNode? = null,
    location: CstLocation? = null
) : CstNode(location) {
    companion object {
        val EMPTY = CstProcNotation()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstProcNotation

        if (inputs != other.inputs) return false
        if (output != other.output) return false

        return true
    }

    override fun hashCode(): Int {
        var result = inputs.hashCode()
        result = 31 * result + (output?.hashCode() ?: 0)
        return result
    }

    override fun toString() = sequence {
        if (inputs.isNotEmpty()) yield("inputs=$inputs")
        if (output != null) yield("output=$output")
    }.joinToString(prefix = "ProcNotation(", postfix = ")")

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitProcNotation(this)

    override fun acceptChildren(visitor: CstVisitor) {
        inputs.forEach { it.accept(visitor) }
        output?.accept(visitor)
    }
}