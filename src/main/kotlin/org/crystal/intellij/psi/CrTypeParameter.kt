package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.elementType
import org.crystal.intellij.lexer.CR_MUL_OP

class CrTypeParameter(node: ASTNode) : CrDefinitionImpl(node), CrTypeDefinition, CrSimpleNameElementHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitTypeParameter(this)

    val isSplat: Boolean
        get() = firstChild.elementType == CR_MUL_OP
}