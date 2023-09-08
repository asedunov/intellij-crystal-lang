package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_CLASS_DEFINITION
import org.crystal.intellij.lang.stubs.api.CrClassStub

class CrClass : CrClassLikeDefinition<CrClass, CrClassStub> {
    constructor(stub: CrClassStub) : super(stub, CR_CLASS_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitClass(this)
}