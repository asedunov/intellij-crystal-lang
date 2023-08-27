package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lexer.CR_FLOAT_LITERAL
import org.crystal.intellij.lexer.CrystalTokenType

class CrFloatLiteralExpression(node: ASTNode) : CrNumericLiteralExpression(node), CrTypeArgument {
    override fun accept(visitor: CrVisitor) = visitor.visitFloatLiteralExpression(this)

    override val tokenType: CrystalTokenType
        get() = CR_FLOAT_LITERAL
}