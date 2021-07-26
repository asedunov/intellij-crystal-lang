package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import kotlin.reflect.KClass

class CrTypeArgumentList(node: ASTNode) : CrElementImpl(node), CrListElement<CrType> {
    override fun accept(visitor: CrVisitor) = visitor.visitTypeArgumentList(this)

    override val elementClass: KClass<CrType>
        get() = CrType::class
}