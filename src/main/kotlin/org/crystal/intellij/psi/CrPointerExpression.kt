package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

open class CrPointerExpression(node: ASTNode) : CrExpression(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitPointerExpression(this)
}