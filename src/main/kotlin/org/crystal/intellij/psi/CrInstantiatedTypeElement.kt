package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_INSTANTIATED_TYPE
import org.crystal.intellij.stubs.api.CrTypeStub

class CrInstantiatedTypeElement : CrTypeElement<CrInstantiatedTypeElement> {
    constructor(stub: CrTypeStub<CrInstantiatedTypeElement>) : super(stub, CR_INSTANTIATED_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitInstantiatedType(this)

    val constructorType: CrTypeElement<*>?
        get() {
            greenStub?.let { return it.childrenStubs.getOrNull(0)?.psi as? CrTypeElement<*> }
            return childOfType()
        }

    val argumentList: CrTypeArgumentList?
        get() = stubChildOfType()
}