package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_NILABLE_TYPE
import org.crystal.intellij.lang.stubs.api.CrTypeStub

class CrNilableTypeElement : CrTypeElement<CrNilableTypeElement> {
    constructor(stub: CrTypeStub<CrNilableTypeElement>) : super(stub, CR_NILABLE_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitNilableType(this)

    val innerType: CrTypeElement<*>?
        get() = stubChildOfType()
}