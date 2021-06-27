package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrLabeledType(node: ASTNode) : CrType(node), CrNameElementHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitLabeledType(this)

    val innerType: CrType?
        get() = childOfType()
}