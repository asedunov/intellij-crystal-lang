package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.config.CrystalLevel
import org.crystal.intellij.lang.config.languageLevel
import org.crystal.intellij.lang.lexer.CR_INTEGER_LITERAL
import org.crystal.intellij.lang.lexer.CrystalTokenType

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
            val suffix = suffix ?: return null
            return CrIntegerKind.valueOf(suffix)
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

    override fun isSuffixDesignator(ch: Char) = ch == 'i' || ch == 'u'
}