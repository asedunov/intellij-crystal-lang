package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable

class CrParameterList(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitParameterList(this)

    val parameters: JBIterable<CrParameter>
        get() = childrenOfType()
}