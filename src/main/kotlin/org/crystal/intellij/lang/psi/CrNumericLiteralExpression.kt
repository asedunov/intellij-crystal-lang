package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.crystal.intellij.lang.lexer.CrystalTokenType

sealed class CrNumericLiteralExpression(node: ASTNode) : CrExpressionImpl(node), CrLiteralExpression {
    protected abstract val tokenType: CrystalTokenType

    private val body: PsiElement?
        get() = findChildByType(tokenType)

    protected val bodyText: String
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
}
