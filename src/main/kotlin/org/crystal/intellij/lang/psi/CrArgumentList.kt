package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import kotlin.reflect.KClass

class CrArgumentList(node: ASTNode) : CrElementImpl(node), CrListElement<CrCallArgument> {
    override fun accept(visitor: CrVisitor) = visitor.visitArgumentList(this)

    override val elementClass: KClass<CrCallArgument>
        get() = CrCallArgument::class
}