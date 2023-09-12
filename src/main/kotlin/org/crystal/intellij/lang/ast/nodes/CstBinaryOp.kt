package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

sealed class CstBinaryOp(
    val left: CstNode,
    val right: CstNode,
    location: CstLocation? = null
) : CstNode(location) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstBinaryOp

        if (left != other.left) return false
        if (right != other.right) return false

        return true
    }

    override fun hashCode(): Int {
        var result = left.hashCode()
        result = 31 * result + right.hashCode()
        return result
    }

    override fun toString() = "$strippedClassName($left, $right)"

    override fun acceptChildren(visitor: CstVisitor) {
        left.accept(visitor)
        right.accept(visitor)
    }
}