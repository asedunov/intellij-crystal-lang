package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrLibrary(node: ASTNode) : CrDefinition(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitLibrary(this)
}