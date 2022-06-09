package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_SPLAT_TYPE
import org.crystal.intellij.stubs.api.CrTypeStub

class CrSplatType : CrType<CrSplatType> {
    constructor(stub: CrTypeStub<CrSplatType>) : super(stub, CR_SPLAT_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitSplatType(this)

    val innerType: CrType<*>?
        get() = stubChildOfType()
}