package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.lexer.CR_FLOAT_LITERAL
import org.crystal.intellij.lang.lexer.CrystalTokenType

class CrFloatLiteralExpression(node: ASTNode) : CrNumericLiteralExpression(node), CrTypeArgument {
    override fun accept(visitor: CrVisitor) = visitor.visitFloatLiteralExpression(this)

    override val tokenType: CrystalTokenType
        get() = CR_FLOAT_LITERAL

    val isFloat32: Boolean
        get() = suffix == "F32"

    override fun isSuffixDesignator(ch: Char) = ch == 'f'
}