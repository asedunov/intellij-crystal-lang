package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrAnnotation(node: ASTNode) : CrDefinitionImpl(node),
                                    CrPathBasedDefinition,
                                    CrTypeDefinition,
                                    CrDefinitionWithFqName {
    override fun accept(visitor: CrVisitor) = visitor.visitAnnotation(this)
}