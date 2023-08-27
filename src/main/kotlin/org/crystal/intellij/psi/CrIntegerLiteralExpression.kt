package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.elementType
import org.crystal.intellij.config.CrystalLevel
import org.crystal.intellij.config.languageLevel
import org.crystal.intellij.lexer.CR_INTEGER_LITERAL
import org.crystal.intellij.lexer.CR_MINUS_OP
import org.crystal.intellij.lexer.CrystalTokenType
import kotlin.math.max

class CrIntegerLiteralExpression(node: ASTNode) : CrNumericLiteralExpression(node), CrTypeArgument {
    override fun accept(visitor: CrVisitor) = visitor.visitIntegerLiteralExpression(this)

    override val tokenType: CrystalTokenType
        get() = CR_INTEGER_LITERAL

    val value: Any?
        get() = kind.parser(valueAsString(true), radix, languageLevel)

    val kind: CrIntegerKind
        get() {
            return explicitKind ?: deduceKind()
        }

    val explicitKind: CrIntegerKind?
        get() {
            val text = bodyText
            val suffixPos = text.findSuffixStart()
            if (suffixPos >= 0) {
                val suffix = text.substring(suffixPos)
                return CrIntegerKind.valueOf(suffix.uppercase())
            }
            return null
        }

    private val isNegated: Boolean
        get() = firstChild?.elementType == CR_MINUS_OP

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

    val valueString: String
        get() = valueAsString(true)

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

        val u64Value = valueString.toULongOrNull(radix)

        if (u64Value == null) {
            if (languageLevel < CrystalLevel.CRYSTAL_1_3) return CrIntegerKind.U64

            val kind = if (negated) CrIntegerKind.I64 else CrIntegerKind.U64
            if (valueString.length > 128) return kind
            val value = (if (negated) "-$valueString" else valueString).toBigInteger(radix)
            if (value !in INT128_RANGE && value in UINT128_RANGE) return CrIntegerKind.U64
            return kind
        }

        var u64Max = Long.MAX_VALUE.toULong()
        if (negated) u64Max++
        if (u64Value > u64Max) return CrIntegerKind.U64

        var u32Max = Int.MAX_VALUE.toUInt()
        if (negated) u32Max++
        if (u64Value > u32Max) return CrIntegerKind.I64

        return CrIntegerKind.I32
    }
}