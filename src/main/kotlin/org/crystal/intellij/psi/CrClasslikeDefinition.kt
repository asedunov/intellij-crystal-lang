package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

sealed class CrClasslikeDefinition(
    node: ASTNode
) : CrDefinitionImpl(node), CrBodyHolder, CrPathBasedDefinition, CrTypeDefinition