package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrAnnotation(node: ASTNode) : CrDefinition(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitAnnotation(this)
}