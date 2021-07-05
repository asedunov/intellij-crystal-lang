package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrAnnotation(node: ASTNode) : CrDefinitionImpl(node),
                                    CrPathBasedDefinition,
                                    CrTypeDefinition {
    override fun accept(visitor: CrVisitor) = visitor.visitAnnotation(this)
}