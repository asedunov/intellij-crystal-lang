package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrCField(node: ASTNode) : CrDefinitionImpl(node), CrTypedDefinition, CrSimpleNameElementHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitCField(this)

    override val type: CrType?
        get() {
            val parent = parent
            return if (parent is CrCFieldGroup) parent.type else super.type
        }
}