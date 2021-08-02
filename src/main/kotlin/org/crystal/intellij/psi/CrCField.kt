package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_C_FIELD_DEFINITION
import org.crystal.intellij.stubs.api.CrCFieldStub

class CrCField : CrDefinitionWithFqNameImpl<CrCField, CrCFieldStub>, CrTypedDefinition, CrSimpleNameElementHolder {
    constructor(stub: CrCFieldStub) : super(stub, CR_C_FIELD_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitCField(this)

    override val type: CrType?
        get() {
            val parent = parent
            return if (parent is CrCFieldGroup) parent.type else super.type
        }

    override val isLocal: Boolean
        get() = false
}