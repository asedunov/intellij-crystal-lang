package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.parser.CR_NAMED_TUPLE_TYPE
import org.crystal.intellij.stubs.api.CrTypeStub

class CrNamedTupleTypeElement : CrTypeElement<CrNamedTupleTypeElement> {
    constructor(stub: CrTypeStub<CrNamedTupleTypeElement>) : super(stub, CR_NAMED_TUPLE_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitNamedTupleType(this)

    val componentTypes: JBIterable<CrLabeledTypeElement>
        get() = stubChildrenOfType()
}