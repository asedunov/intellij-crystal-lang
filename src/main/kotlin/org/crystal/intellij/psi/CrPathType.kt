package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_PATH_TYPE
import org.crystal.intellij.stubs.api.CrTypeStub

class CrPathType : CrType<CrPathType> {
    constructor(stub: CrTypeStub<CrPathType>) : super(stub, CR_PATH_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitPathType(this)

    val path: CrPathNameElement?
        get() = stubChildOfType()
}