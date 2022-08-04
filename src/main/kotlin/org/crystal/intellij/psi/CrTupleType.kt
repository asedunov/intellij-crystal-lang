package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.parser.CR_TUPLE_TYPE
import org.crystal.intellij.stubs.api.CrTypeStub

class CrTupleType : CrType<CrTupleType> {
    constructor(stub: CrTypeStub<CrTupleType>) : super(stub, CR_TUPLE_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitTupleType(this)

    val componentTypes: JBIterable<CrType<*>>
        get() = stubChildrenOfType()
}