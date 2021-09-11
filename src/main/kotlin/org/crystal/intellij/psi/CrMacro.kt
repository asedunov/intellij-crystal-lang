package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrMacro(node: ASTNode) : CrDefinitionImpl(node), CrSimpleNameElementHolder, CrFunctionLikeDefinition {
    override fun accept(visitor: CrVisitor) = visitor.visitMacro(this)
}