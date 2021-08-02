package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_LABELED_TYPE
import org.crystal.intellij.stubs.api.CrTypeStub

class CrLabeledType : CrType, CrNamedElement, CrSimpleNameElementHolder {
    constructor(stub: CrTypeStub) : super(stub, CR_LABELED_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitLabeledType(this)

    val innerType: CrType?
        get() = stubChildOfType()
}