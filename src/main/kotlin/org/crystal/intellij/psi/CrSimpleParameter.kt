package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_SIMPLE_NAME_ELEMENT
import org.crystal.intellij.parser.CR_SIMPLE_PARAMETER_DEFINITION
import org.crystal.intellij.stubs.api.CrSimpleParameterStub

class CrSimpleParameter : CrDefinitionImpl<CrSimpleParameterStub>, CrParameter, CrDefinitionWithInitializer {
    constructor(stub: CrSimpleParameterStub) : super(stub, CR_SIMPLE_PARAMETER_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitSimpleParameter(this)

    override val kind: CrParameterKind
        get() = greenStub?.kind ?: super.kind

    val externalNameElement: CrSimpleNameElement?
        get() {
            greenStub?.let {
                val nameStubs = it.getChildrenByType(CR_SIMPLE_NAME_ELEMENT, CrSimpleNameElement.EMPTY_ARRAY)
                return if (nameStubs.size > 1) nameStubs.first() else null
            }

            val firstNameElement = childOfType<CrSimpleNameElement>()
            val secondNameElement = firstNameElement?.skipWhitespacesAndCommentsForward()
            return if (secondNameElement is CrSimpleNameElement) return firstNameElement else null
        }

    override val nameElement: CrSimpleNameElement?
        get() {
            greenStub?.let {
                return it.getChildrenByType(CR_SIMPLE_NAME_ELEMENT, CrSimpleNameElement.EMPTY_ARRAY).lastOrNull()
            }

            val externalNameElement = externalNameElement ?: return childOfType()
            return externalNameElement.nextSiblingOfType()
        }
}