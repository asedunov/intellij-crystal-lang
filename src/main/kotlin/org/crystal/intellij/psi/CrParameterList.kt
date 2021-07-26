package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import kotlin.reflect.KClass

class CrParameterList(node: ASTNode) : CrElementImpl(node), CrListElement<CrSimpleParameter> {
    override fun accept(visitor: CrVisitor) = visitor.visitParameterList(this)

    override val elementClass: KClass<CrSimpleParameter>
        get() = CrSimpleParameter::class
}