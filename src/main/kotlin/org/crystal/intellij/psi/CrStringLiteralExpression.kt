package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.ElementManipulators
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.elementType
import org.crystal.intellij.lexer.CR_STRING_END
import org.crystal.intellij.lexer.CR_STRING_START

class CrStringLiteralExpression(node: ASTNode) : CrExpressionImpl(node), CrStringValueHolder, PsiLanguageInjectionHost {
    override fun accept(visitor: CrVisitor) = visitor.visitStringLiteralExpression(this)

    val openQuote: PsiElement
        get() = firstChild.takeIf { it.elementType == CR_STRING_START }!!

    private val closeQuote: PsiElement
        get() = lastChild.takeIf { it.elementType == CR_STRING_END }!!

    val valueRangeInElement: TextRange
        get() {
            val from = openQuote.textLength
            val to = textLength - closeQuote.textLength
            return TextRange(from, to)
        }

    override val stringValue: String?
        get() = buildString {
            for (child in allChildren()) {
                val et = child.elementType
                if (et == CR_STRING_START || et == CR_STRING_END) continue
                when (child) {
                    is CrStringValueHolder -> append(child.stringValue ?: return null)
                    is CrCharValueHolder -> append(child.charValue ?: return null)
                    is PsiWhiteSpace -> continue
                    else -> return null
                }
            }
        }

    override fun isValidHost() = true

    override fun updateText(text: String): CrStringLiteralExpression? =
        ElementManipulators.handleContentChange(this, text)

    override fun createLiteralTextEscaper(): CrStringLiteralEscaper =
        CrStringLiteralEscaper(this)
}