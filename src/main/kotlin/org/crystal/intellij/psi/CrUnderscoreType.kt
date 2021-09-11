package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_UNDERSCORE_TYPE
import org.crystal.intellij.stubs.api.CrTypeStub

class CrUnderscoreType : CrType {
    constructor(stub: CrTypeStub) : super(stub, CR_UNDERSCORE_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitUnderscoreType(this)
}