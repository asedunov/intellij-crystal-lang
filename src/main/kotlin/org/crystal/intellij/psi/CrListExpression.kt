package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import kotlin.reflect.KClass

class CrListExpression(node: ASTNode) : CrExpressionImpl(node), CrListElement<CrExpression> {
    override fun accept(visitor: CrVisitor) = visitor.visitListExpression(this)

    override val elementClass: KClass<CrExpression>
        get() = CrExpression::class
}