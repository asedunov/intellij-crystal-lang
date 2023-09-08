package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable

class CrExceptionHandler(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitExceptionHandler(this)

    val rescueClauses: JBIterable<CrRescueClause>
        get() = childrenOfType()

    val elseClause: CrElseClause?
        get() = childOfType()

    val ensureClause: CrEnsureClause?
        get() = childOfType()

    val block: CrBlockExpression?
        get() = parent as? CrBlockExpression
}