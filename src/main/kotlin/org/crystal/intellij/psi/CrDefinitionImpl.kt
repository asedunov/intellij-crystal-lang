package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.presentation.CrystalDefinitionPresentation

sealed class CrDefinitionImpl(node: ASTNode) : CrExpressionImpl(node), CrDefinition, CrNamedElement {
    override fun getPresentation() = CrystalDefinitionPresentation(this)
}