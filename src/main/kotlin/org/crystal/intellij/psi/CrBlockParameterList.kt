package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import kotlin.reflect.KClass

class CrBlockParameterList(node: ASTNode) : CrElementImpl(node), CrListElement<CrParameter> {
    override fun accept(visitor: CrVisitor) = visitor.visitBlockParameterList(this)

    override val elementClass: KClass<CrParameter>
        get() = CrParameter::class
}