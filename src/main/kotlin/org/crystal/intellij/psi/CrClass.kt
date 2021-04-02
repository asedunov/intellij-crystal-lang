package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrClass(node: ASTNode) : CrDefinition(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitClass(this)
}