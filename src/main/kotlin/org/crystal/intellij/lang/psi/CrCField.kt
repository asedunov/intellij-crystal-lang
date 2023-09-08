package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_C_FIELD_DEFINITION
import org.crystal.intellij.lang.stubs.api.CrCFieldStub

class CrCField : CrDefinitionWithFqNameImpl<CrCField, CrCFieldStub>, CrTypedDefinition, CrSimpleNameElementHolder {
    constructor(stub: CrCFieldStub) : super(stub, CR_C_FIELD_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitCField(this)

    override val type: CrTypeElement<*>?
        get() {
            val parent = parent
            return if (parent is CrCFieldGroup) parent.type else super.type
        }

    override val isLocal: Boolean
        get() = false
}