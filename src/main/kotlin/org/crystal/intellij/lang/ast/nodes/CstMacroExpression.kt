package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstMacroExpression(
    val exp: CstNode,
    val isOutput: Boolean = true,
    location: CstLocation? = null
) : CstNode(location) {
    fun copy(
        exp: CstNode = this.exp,
        isOutput: Boolean = this.isOutput,
        location: CstLocation? = this.location
    ) = CstMacroExpression(exp, isOutput, location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstMacroExpression

        if (exp != other.exp) return false
        if (isOutput != other.isOutput) return false

        return true
    }

    override fun hashCode(): Int {
        var result = exp.hashCode()
        result = 31 * result + isOutput.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("MacroExpression(")
        append("exp=$exp")
        if (isOutput) append(", isOutput")
        append(")")
    }

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitMacroExpression(this)

    override fun acceptChildren(visitor: CstVisitor) {
        exp.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformMacroExpression(this)
}