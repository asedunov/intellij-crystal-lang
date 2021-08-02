package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_STATIC_ARRAY_TYPE
import org.crystal.intellij.stubs.api.CrTypeStub

class CrStaticArrayType : CrType {
    constructor(stub: CrTypeStub) : super(stub, CR_STATIC_ARRAY_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitStaticArrayType(this)

    val elementType: CrType?
        get() = stubChildOfType()
}