package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_FUNCTION_DEFINITION
import org.crystal.intellij.parser.CR_SIMPLE_NAME_ELEMENT
import org.crystal.intellij.stubs.api.CrFunctionStub

class CrFunction : CrDefinitionWithFqNameImpl<CrFunction, CrFunctionStub>, CrFunctionLikeDefinition, CrSimpleNameElementHolder {
    constructor(stub: CrFunctionStub) : super(stub, CR_FUNCTION_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitFunction(this)

    val externalNameElement: CrSimpleNameElement?
        get() {
            greenStub?.let {
                return it.getChildrenByType(CR_SIMPLE_NAME_ELEMENT, CrSimpleNameElement.EMPTY_ARRAY).getOrNull(1)
            }
            return nameElement?.nextSiblingOfType()
        }

    val isVariadic: Boolean
        get() {
            greenStub?.let { return it.isVariadic }
            return parameterList?.isVariadic == true
        }
}