package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_NILABLE_TYPE
import org.crystal.intellij.stubs.api.CrTypeStub

class CrNilableType : CrType<CrNilableType> {
    constructor(stub: CrTypeStub<CrNilableType>) : super(stub, CR_NILABLE_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitNilableType(this)

    val innerType: CrType<*>?
        get() = stubChildOfType()
}