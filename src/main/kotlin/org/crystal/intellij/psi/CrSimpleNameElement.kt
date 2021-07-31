package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.elementType
import org.crystal.intellij.lexer.CR_CLASS_VAR
import org.crystal.intellij.lexer.CR_GLOBAL_VAR
import org.crystal.intellij.lexer.CR_INSTANCE_VAR

class CrSimpleNameElement(node: ASTNode) : CrNameElement(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitSimpleNameElement(this)

    override fun getName(): String? {
        val e = firstChild
        if (e is CrStringLiteralExpression) return e.stringValue
        val name = text
        return when (e.elementType) {
            CR_INSTANCE_VAR, CR_GLOBAL_VAR -> name.substring(1)
            CR_CLASS_VAR -> name.substring(2)
            else -> name
        }
    }

    val tokenType: IElementType?
        get() = firstChild?.elementType

    val isQuestion: Boolean
        get() = name?.lastOrNull() == '?'

    val isExclamation: Boolean
        get() = name?.lastOrNull() == '?'
}