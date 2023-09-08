package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_SIMPLE_NAME_ELEMENT
import org.crystal.intellij.lang.parser.CR_SIMPLE_PARAMETER_DEFINITION
import org.crystal.intellij.lang.stubs.api.CrSimpleParameterStub

class CrSimpleParameter :
    CrDefinitionImpl<CrSimpleParameterStub>,
    CrParameter,
    CrDefinitionWithInitializer,
    CrExternalNameElementHolder,
    CrSyntheticArgHolder {
    constructor(stub: CrSimpleParameterStub) : super(stub, CR_SIMPLE_PARAMETER_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitSimpleParameter(this)

    override val kind: CrParameterKind
        get() = greenStub?.kind ?: super.kind

    override val externalNameElement: CrSimpleNameElement?
        get() {
            greenStub?.let {
                val nameStubs = it.getChildrenByType(CR_SIMPLE_NAME_ELEMENT, CrSimpleNameElement.EMPTY_ARRAY)
                return if (nameStubs.size > 1) nameStubs.first() else null
            }

            val firstNameElement = childOfType<CrSimpleNameElement>()
            val secondNameElement = firstNameElement?.nextSiblingOfType<CrSimpleNameElement>()
            return if (secondNameElement != null) firstNameElement else null
        }

    val externalName: String?
        get() = externalNameElement?.name ?: name

    override val nameElement: CrSimpleNameElement?
        get() {
            greenStub?.let {
                return it.getChildrenByType(CR_SIMPLE_NAME_ELEMENT, CrSimpleNameElement.EMPTY_ARRAY).lastOrNull()
            }

            val externalNameElement = externalNameElement ?: return childOfType()
            return externalNameElement.nextSiblingOfType()
        }
}