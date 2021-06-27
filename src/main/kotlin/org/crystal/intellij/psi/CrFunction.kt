package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrFunction(node: ASTNode) : CrDefinitionImpl(node), CrFunctionLikeDefinition {
    override fun accept(visitor: CrVisitor) = visitor.visitFunction(this)

    val externalNameElement: CrNameElement?
        get() = nameElement?.nextSiblingOfType()
}