package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrClass(node: ASTNode) : CrClasslikeDefinition(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitClass(this)
}