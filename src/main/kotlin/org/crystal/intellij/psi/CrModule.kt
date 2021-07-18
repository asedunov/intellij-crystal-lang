package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrModule(node: ASTNode) : CrClasslikeDefinition(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitModule(this)
}