package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstVisitor

class CstAsmOperand(
    val constraint: String,
    val exp: CstNode,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstAsmOperand

        if (constraint != other.constraint) return false
        if (exp != other.exp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = constraint.hashCode()
        result = 31 * result + exp.hashCode()
        return result
    }

    override fun toString() = "AsmOperand(constraint=$constraint, exp=$exp)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitAsmOperand(this)

    override fun acceptChildren(visitor: CstVisitor) {
        exp.accept(visitor)
    }
}