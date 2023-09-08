package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrMacroIfStatement(node: ASTNode) : CrMacroIfUnlessStatement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitMacroIfStatement(this)

    val elsifStatement: CrMacroIfStatement?
        get() = thenClause?.nextSiblingOfType()
}