package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.elementType
import org.crystal.intellij.lexer.CR_AND_OP
import org.crystal.intellij.lexer.CR_EXP_OP
import org.crystal.intellij.lexer.CR_MUL_OP

sealed class CrParameter(node: ASTNode) : CrDefinitionImpl(node), CrSimpleNameElementHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitParameter(this)

    val kind: CrParameterKind
        get() = when (firstChild.elementType) {
            CR_MUL_OP -> CrParameterKind.SPLAT
            CR_EXP_OP -> CrParameterKind.DOUBLE_SPLAT
            CR_AND_OP -> CrParameterKind.BLOCK
            else -> CrParameterKind.ORDINARY
        }
}