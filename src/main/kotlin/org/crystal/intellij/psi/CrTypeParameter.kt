package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.elementType
import org.crystal.intellij.lexer.CR_MUL_OP
import org.crystal.intellij.parser.CR_TYPE_PARAMETER_DEFINITION
import org.crystal.intellij.stubs.api.CrDefinitionStub

class CrTypeParameter : CrDefinitionImpl<CrDefinitionStub<*>>, CrSimpleNameElementHolder {
    constructor(stub: CrDefinitionStub<*>) : super(stub, CR_TYPE_PARAMETER_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitTypeParameter(this)

    val isSplat: Boolean
        get() = firstChild.elementType == CR_MUL_OP
}