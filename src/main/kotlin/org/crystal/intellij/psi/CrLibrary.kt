package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrLibrary(node: ASTNode) : CrDefinitionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitLibrary(this)
}