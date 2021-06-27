package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable

class CrNamedTupleType(node: ASTNode) : CrType(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitNamedTupleType(this)

    val componentTypes: JBIterable<CrLabeledType>
        get() = childrenOfType()
}