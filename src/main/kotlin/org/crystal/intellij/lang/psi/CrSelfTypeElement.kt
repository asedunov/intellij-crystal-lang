package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_SELF_TYPE
import org.crystal.intellij.lang.stubs.api.CrTypeStub

class CrSelfTypeElement : CrTypeElement<CrSelfTypeElement> {
    constructor(stub: CrTypeStub<CrSelfTypeElement>) : super(stub, CR_SELF_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitSelfType(this)
}