package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable

class CrTupleType(node: ASTNode) : CrType(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitTupleType(this)

    val componentTypes: JBIterable<CrType>
        get() = childrenOfType()
}