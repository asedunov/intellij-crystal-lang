package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrHashType(node: ASTNode) : CrType(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitHashType(this)

    val leftType: CrType?
        get() = firstChild as? CrType

    val rightType: CrType?
        get() = leftType?.nextSiblingOfType()
}