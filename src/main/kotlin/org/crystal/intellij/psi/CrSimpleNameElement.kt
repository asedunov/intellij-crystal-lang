package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.elementType

class CrSimpleNameElement(node: ASTNode) : CrNameElement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitSimpleNameElement(this)

    override fun getName(): String? {
        val e = firstChild
        return if (e is CrStringLiteralExpression) e.stringValue else text
    }

    val tokenType: IElementType?
        get() = firstChild?.elementType

    val isQuestion: Boolean
        get() = name?.lastOrNull() == '?'

    val isExclamation: Boolean
        get() = name?.lastOrNull() == '?'
}