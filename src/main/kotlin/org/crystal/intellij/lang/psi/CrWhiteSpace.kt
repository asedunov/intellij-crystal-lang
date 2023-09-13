package org.crystal.intellij.lang.psi

import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.tree.IElementType
import org.crystal.intellij.lang.lexer.CR_NEWLINE

class CrWhiteSpace(type: IElementType, text: CharSequence) : CrCustomLeafElement(type, text), PsiWhiteSpace {
    override fun getName() = text

    override fun accept(visitor: PsiElementVisitor) = visitor.visitWhiteSpace(this)

    override fun accept(visitor: CrVisitor) = visitor.visitWhiteSpace(this)

    val isNewLine: Boolean
        get() = elementType == CR_NEWLINE
}