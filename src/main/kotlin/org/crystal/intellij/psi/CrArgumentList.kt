package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import kotlin.reflect.KClass

class CrArgumentList(node: ASTNode) : CrElementImpl(node), CrListElement<CrExpression> {
    override fun accept(visitor: CrVisitor) = visitor.visitArgumentList(this)

    override val elementClass: KClass<CrExpression>
        get() = CrExpression::class
}