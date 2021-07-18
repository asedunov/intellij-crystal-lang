package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrStruct(node: ASTNode) : CrClasslikeDefinition(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitStruct(this)
}