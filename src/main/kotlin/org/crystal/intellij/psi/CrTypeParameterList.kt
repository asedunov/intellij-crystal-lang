package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable

class CrTypeParameterList(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitTypeParameterList(this)

    val typeParameters: JBIterable<CrTypeParameter>
        get() = childrenOfType()
}