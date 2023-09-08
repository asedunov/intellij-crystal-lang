package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_SPLAT_TYPE
import org.crystal.intellij.lang.stubs.api.CrTypeStub

class CrSplatTypeElement : CrTypeElement<CrSplatTypeElement> {
    constructor(stub: CrTypeStub<CrSplatTypeElement>) : super(stub, CR_SPLAT_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitSplatType(this)

    val innerType: CrTypeElement<*>?
        get() = stubChildOfType()
}