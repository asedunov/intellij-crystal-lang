package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lexer.CR_IDENTIFIER
import org.crystal.intellij.parser.CR_VARIABLE_DEFINITION
import org.crystal.intellij.stubs.api.CrVariableStub

class CrVariable : CrDefinitionWithFqNameImpl<CrVariable, CrVariableStub>, CrDefinitionWithDefault, CrSimpleNameElementHolder {
    constructor(stub: CrVariableStub) : super(stub, CR_VARIABLE_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitVariable(this)

    override val isLocal: Boolean
        get() = greenStub == null && parentStubOrPsi() !is CrFile && nameElement?.innerElementType == CR_IDENTIFIER
}