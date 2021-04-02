package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrAlias(node: ASTNode) : CrDefinition(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitAlias(this)
}