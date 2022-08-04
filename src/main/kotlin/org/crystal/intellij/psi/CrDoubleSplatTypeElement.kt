package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_DOUBLE_SPLAT_TYPE
import org.crystal.intellij.stubs.api.CrTypeStub

class CrDoubleSplatTypeElement : CrTypeElement<CrDoubleSplatTypeElement> {
    constructor(stub: CrTypeStub<CrDoubleSplatTypeElement>) : super(stub, CR_DOUBLE_SPLAT_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitDoubleSplatType(this)

    val innerType: CrTypeElement<*>?
        get() = stubChildOfType()
}