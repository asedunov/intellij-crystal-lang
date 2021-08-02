package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_METHOD_DEFINITION
import org.crystal.intellij.stubs.api.CrMethodStub

class CrMethod : CrDefinitionWithFqNameImpl<CrMethod, CrMethodStub>, CrFunctionLikeDefinition, CrSimpleNameElementHolder {
    constructor(stub: CrMethodStub) : super(stub, CR_METHOD_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitMethod(this)

    val receiver: CrMethodReceiver?
        get() = firstChild?.skipWhitespacesAndCommentsForward() as? CrMethodReceiver
}