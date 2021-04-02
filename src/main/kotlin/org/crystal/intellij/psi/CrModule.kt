package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrModule(node: ASTNode) : CrDefinition(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitModule(this)
}