package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_METACLASS_TYPE
import org.crystal.intellij.stubs.api.CrTypeStub

class CrMetaclassTypeElement : CrTypeElement<CrMetaclassTypeElement> {
    constructor(stub: CrTypeStub<CrMetaclassTypeElement>) : super(stub, CR_METACLASS_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitMetaclassType(this)

    val innerType: CrTypeElement<*>?
        get() = stubChildOfType()
}