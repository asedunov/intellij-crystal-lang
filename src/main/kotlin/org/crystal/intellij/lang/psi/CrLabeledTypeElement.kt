package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_LABELED_TYPE
import org.crystal.intellij.lang.stubs.api.CrTypeStub

class CrLabeledTypeElement : CrTypeElement<CrLabeledTypeElement>, CrNamedElement, CrSimpleNameElementHolder {
    constructor(stub: CrTypeStub<CrLabeledTypeElement>) : super(stub, CR_LABELED_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitLabeledType(this)

    val innerType: CrTypeElement<*>?
        get() = stubChildOfType()
}