package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_STATIC_ARRAY_TYPE
import org.crystal.intellij.stubs.api.CrTypeStub

class CrStaticArrayTypeElement : CrTypeElement<CrStaticArrayTypeElement> {
    constructor(stub: CrTypeStub<CrStaticArrayTypeElement>) : super(stub, CR_STATIC_ARRAY_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitStaticArrayType(this)

    val elementType: CrTypeElement<*>?
        get() = stubChildOfType()
}