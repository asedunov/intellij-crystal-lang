package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import kotlin.reflect.KClass

class CrMultiParameter(node: ASTNode) : CrElementImpl(node), CrParameter, CrListElement<CrSimpleParameter> {
    override fun accept(visitor: CrVisitor) = visitor.visitMultiParameter(this)

    override val elementClass: KClass<CrSimpleParameter>
        get() = CrSimpleParameter::class
}