package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrMethod(node: ASTNode) : CrDefinition(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitMethod(this)
}