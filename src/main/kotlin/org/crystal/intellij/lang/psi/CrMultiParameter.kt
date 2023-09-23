package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import kotlin.reflect.KClass

class CrMultiParameter(
    node: ASTNode
) : CrElementImpl(node), CrParameter, CrListElement<CrParameter>, CrSyntheticArgHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitMultiParameter(this)

    override val elementClass: KClass<CrParameter>
        get() = CrParameter::class
}