package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable

class CrTypeArgumentList(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitTypeArgumentList(this)

    val componentTypes: JBIterable<CrType>
        get() = childrenOfType()
}