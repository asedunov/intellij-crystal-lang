package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_PATH_TYPE
import org.crystal.intellij.lang.stubs.api.CrTypeStub

class CrPathTypeElement : CrTypeElement<CrPathTypeElement> {
    constructor(stub: CrTypeStub<CrPathTypeElement>) : super(stub, CR_PATH_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitPathType(this)

    val path: CrPathNameElement?
        get() = stubChildOfType()
}