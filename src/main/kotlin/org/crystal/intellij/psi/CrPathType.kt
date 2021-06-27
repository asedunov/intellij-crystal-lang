package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrPathType(node: ASTNode) : CrType(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitPathType(this)

    val path: CrPath?
        get() = childOfType()
}