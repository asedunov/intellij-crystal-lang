package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.lang.parser.CR_UNION_TYPE
import org.crystal.intellij.lang.stubs.api.CrTypeStub

class CrUnionTypeElement : CrTypeElement<CrUnionTypeElement> {
    constructor(stub: CrTypeStub<CrUnionTypeElement>) : super(stub, CR_UNION_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitUnionType(this)

    val componentTypes: JBIterable<CrTypeElement<*>>
        get() = stubChildrenOfType()
}