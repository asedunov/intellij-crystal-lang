package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_SIMPLE_NAME_ELEMENT
import org.crystal.intellij.parser.CR_VARIABLE_DEFINITION
import org.crystal.intellij.stubs.api.CrVariableStub

class CrVariable :
    CrDefinitionWithFqNameImpl<CrVariable, CrVariableStub>,
    CrDefinitionWithInitializer,
    CrSimpleNameElementHolder,
    CrExternalNameElementHolder{
    constructor(stub: CrVariableStub) : super(stub, CR_VARIABLE_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitVariable(this)

    override val isLocal: Boolean
        get() = greenStub == null &&
                !isTopLevel &&
                nameElement?.kind == CrNameKind.IDENTIFIER

    override val externalNameElement: CrSimpleNameElement?
        get() {
            greenStub?.let {
                return it.getChildrenByType(CR_SIMPLE_NAME_ELEMENT, CrSimpleNameElement.EMPTY_ARRAY).getOrNull(1)
            }
            return nameElement?.nextSiblingOfType()
        }
}