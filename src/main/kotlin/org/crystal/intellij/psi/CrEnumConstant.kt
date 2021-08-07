package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrEnumConstant(node: ASTNode) : CrDefinitionImpl(node), CrSimpleNameElementHolder, CrDefinitionWithFqName {
    override fun accept(visitor: CrVisitor) = visitor.visitEnumConstant(this)
}