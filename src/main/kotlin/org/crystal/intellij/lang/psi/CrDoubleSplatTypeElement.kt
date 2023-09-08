package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_DOUBLE_SPLAT_TYPE
import org.crystal.intellij.lang.stubs.api.CrTypeStub

class CrDoubleSplatTypeElement : CrTypeElement<CrDoubleSplatTypeElement> {
    constructor(stub: CrTypeStub<CrDoubleSplatTypeElement>) : super(stub, CR_DOUBLE_SPLAT_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitDoubleSplatType(this)

    val innerType: CrTypeElement<*>?
        get() = stubChildOfType()
}