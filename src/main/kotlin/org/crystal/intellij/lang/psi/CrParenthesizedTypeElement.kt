package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_PARENTHESIZED_TYPE
import org.crystal.intellij.lang.stubs.api.CrTypeStub

class CrParenthesizedTypeElement : CrTypeElement<CrParenthesizedTypeElement> {
    constructor(stub: CrTypeStub<CrParenthesizedTypeElement>) : super(stub, CR_PARENTHESIZED_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitParenthesizedType(this)

    val innerType: CrTypeElement<*>?
        get() = stubChildOfType()
}