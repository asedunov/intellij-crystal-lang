package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_PROC_TYPE
import org.crystal.intellij.lang.stubs.api.CrTypeStub

class CrProcTypeElement : CrTypeElement<CrProcTypeElement> {
    constructor(stub: CrTypeStub<CrProcTypeElement>) : super(stub, CR_PROC_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitProcType(this)

    val inputList: CrTypeArgumentList?
        get() = stubChildOfType()

    val outputType: CrTypeElement<*>?
        get() = stubChildOfType()
}