package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.crystal.intellij.lexer.CR_INTEGER_LITERAL
import org.crystal.intellij.lexer.CR_MINUS_OP
import kotlin.math.max

class CrIntegerLiteralExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitIntegerLiteralExpression(this)

    val value: Any?
        get() = kind.parser(valueAsString(true), radix)

    val kind: CrIntegerKind
        get() {
            val text = bodyText
            val suffixPos = text.findSuffixStart()
            if (suffixPos >= 0) {
                val suffix = text.substring(suffixPos)
                return CrIntegerKind.valueOf(suffix.uppercase())
            }
            return deduceKind()
        }

    private val radix: Int
        get() = when(prefix) {
            "0b" -> 2
            "0o", "0" -> 8
            "0x" -> 16
            else -> 10
        }

    private val isNegated: Boolean
        get() = firstChild?.elementType == CR_MINUS_OP

    private val body: PsiElement?
        get() = findChildByType(CR_INTEGER_LITERAL)

    private val bodyText: String
        get() = body?.text ?: ""

    private fun valueAsString(withSign: Boolean) = buildString {
        if (withSign && isNegated) append('-')
        val text = bodyText
        val n = text.length
        val from = prefix.length
        for (i in from until n) {
            when (val ch = text[i]) {
                '_' -> continue
                'i', 'I', 'u', 'U' -> break
                else -> append(ch)
            }
        }
    }

    private val prefix: String
        get() {
            val text = bodyText
            if (text.length < 2 || text[0] != '0') return ""
            return when (text[1]) {
                'b', 'o', 'x' -> text.substring(0, 2)
                in '0'..'9' -> "0"
                else -> ""
            }
        }

    private fun String.findSuffixStart(): Int {
        val text = bodyText
        for (i in lastIndex downTo max(lastIndex - 3, 0)) {
            val ch = text[i].lowercaseChar()
            if (ch == 'i' || ch == 'u') return i
        }
        return -1
    }

    private fun deduceKind(): CrIntegerKind {
        val valueString = valueAsString(false)
        val negated = isNegated

        val u64Value = valueString.toULongOrNull(radix) ?: return CrIntegerKind.U64

        var u64Max = Long.MAX_VALUE.toULong()
        if (negated) u64Max++
        if (u64Value > u64Max) return CrIntegerKind.U64

        var u32Max = Int.MAX_VALUE.toUInt()
        if (negated) u32Max++
        if (u64Value > u32Max) return CrIntegerKind.I64

        return CrIntegerKind.I32
    }
}