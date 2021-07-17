package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.elementType
import org.crystal.intellij.lexer.CR_SELF

class CrNameElement(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitNameElement(this)

    override fun getName(): String? = text

    val tokenType: IElementType?
        get() = firstChild?.elementType

    val isSelf: Boolean
        get() = tokenType == CR_SELF

    val isQuestionOrExclamation: Boolean
        get() {
            val last = name?.lastOrNull()
            return last == '?' || last == '!'
        }
}