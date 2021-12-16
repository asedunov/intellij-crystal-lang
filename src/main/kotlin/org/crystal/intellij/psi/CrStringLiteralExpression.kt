package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.elementType
import org.crystal.intellij.lexer.CR_STRING_END
import org.crystal.intellij.lexer.CR_STRING_START
import org.crystal.intellij.references.CrRequireReferenceSet

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

    val stringParent: PsiElement?
        get() {
            var p = parent
            if (p is CrConcatenatedStringLiteralExpression) p = p.parent
            return p
        }

    override fun getReferences(): Array<out PsiReference> {
        if (stringParent !is CrRequireExpression || stringValue == null) return PsiReference.EMPTY_ARRAY
        return CachedValuesManager.getCachedValue(this) {
            CachedValueProvider.Result.create(
                CrRequireReferenceSet(this).allReferences,
                PsiModificationTracker.MODIFICATION_COUNT
            )
        }
    }

    override fun isValidHost() = true

    override fun updateText(text: String): CrStringLiteralExpression? =
        ElementManipulators.handleContentChange(this, text)

    override fun createLiteralTextEscaper(): CrStringLiteralEscaper =
        CrStringLiteralEscaper(this)
}