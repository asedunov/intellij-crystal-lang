package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.parser.CR_UNION_TYPE
import org.crystal.intellij.stubs.api.CrTypeStub

class CrUnionTypeElement : CrTypeElement<CrUnionTypeElement> {
    constructor(stub: CrTypeStub<CrUnionTypeElement>) : super(stub, CR_UNION_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitUnionType(this)

    val componentTypes: JBIterable<CrTypeElement<*>>
        get() = stubChildrenOfType()
}