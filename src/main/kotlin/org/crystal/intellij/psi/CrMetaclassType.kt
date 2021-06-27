package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrMetaclassType(node: ASTNode) : CrType(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitMetaclassType(this)

    val innerType: CrType?
        get() = childOfType()
}