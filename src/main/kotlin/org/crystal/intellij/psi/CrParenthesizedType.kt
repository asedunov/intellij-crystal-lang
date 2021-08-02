package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_PARENTHESIZED_TYPE
import org.crystal.intellij.stubs.api.CrTypeStub

class CrParenthesizedType : CrType {
    constructor(stub: CrTypeStub) : super(stub, CR_PARENTHESIZED_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitParenthesizedType(this)

    val innerType: CrType?
        get() = stubChildOfType()
}