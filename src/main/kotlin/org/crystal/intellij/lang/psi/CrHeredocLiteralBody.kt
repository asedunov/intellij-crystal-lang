package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode

class CrHeredocLiteralBody(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitHeredocLiteralBody(this)

    private val indent: CrHeredocIndent?
        get() = childOfType()

    val indentSize: Int
        get() = indent?.size ?: 0

    val endId: CrHeredocEndId?
        get() = childOfType()
}