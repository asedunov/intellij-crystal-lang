package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import kotlin.reflect.KClass

class CrTypeParameterList(node: ASTNode) : CrElementImpl(node), CrListElement<CrTypeParameter> {
    override fun accept(visitor: CrVisitor) = visitor.visitTypeParameterList(this)

    override val elementClass: KClass<CrTypeParameter>
        get() = CrTypeParameter::class
}