package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_SELF_TYPE
import org.crystal.intellij.stubs.api.CrTypeStub

class CrSelfType : CrType<CrSelfType> {
    constructor(stub: CrTypeStub<CrSelfType>) : super(stub, CR_SELF_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitSelfType(this)
}