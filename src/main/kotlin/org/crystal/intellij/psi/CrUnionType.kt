package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable

class CrUnionType(node: ASTNode) : CrType(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitUnionType(this)

    val componentTypes: JBIterable<CrType>
        get() = childrenOfType()
}