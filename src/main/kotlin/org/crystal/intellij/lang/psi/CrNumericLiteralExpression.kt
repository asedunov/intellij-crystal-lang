package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.crystal.intellij.lang.lexer.CR_MINUS_OP
import org.crystal.intellij.lang.lexer.CrystalTokenType

sealed class CrNumericLiteralExpression(node: ASTNode) : CrExpressionImpl(node) {
    protected abstract val tokenType: CrystalTokenType

    protected val isNegated: Boolean
        get() = firstChild?.elementType == CR_MINUS_OP

    private val body: PsiElement?
        get() = findChildByType(tokenType)

    private val bodyText: String
        get() = body?.text ?: ""

    val prefix: String
        get() {
            val text = bodyText
            if (text.length < 2 || text[0] != '0') return ""
            return when (text[1]) {
                'b', 'o', 'x' -> text.substring(0, 2)
                in '0'..'9' -> "0"
                else -> ""
            }
        }

    val radix: Int
        get() = when(prefix) {
            "0b" -> 2
            "0o", "0" -> 8
            "0x" -> 16
            else -> 10
        }

    protected abstract fun isSuffixDesignator(ch: Char): Boolean

    private fun findSuffixStart(): Int {
        val text = bodyText
        for (i in text.lastIndex downTo kotlin.math.max(text.lastIndex - 3, 0)) {
            if (isSuffixDesignator(text[i])) return i
        }
        return -1
    }

    protected val suffix: String?
        get() {
            val text = bodyText
            val suffixPos = findSuffixStart()
            return if (suffixPos >= 0) text.substring(suffixPos).uppercase() else null
        }

    protected fun valueAsString(withSign: Boolean) = buildString {
        if (withSign && isNegated) append('-')
        val text = bodyText
        val n = text.length
        val from = prefix.length
        for (i in from until n) {
            val ch = text[i]
            when {
                ch == '_' -> continue
                isSuffixDesignator(ch) -> break
                else -> append(ch)
            }
        }
    }

    val valueString: String
        get() = valueAsString(true)
}
