package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrParenthesizedType(node: ASTNode) : CrType(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitParenthesizedType(this)

    val innerType: CrType?
        get() = childOfType()
}