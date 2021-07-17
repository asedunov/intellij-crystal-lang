package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.crystal.intellij.presentation.CrystalDefinitionPresentation

sealed class CrDefinitionImpl(node: ASTNode) : CrExpressionImpl(node), CrDefinition {
    override fun getNameIdentifier() = nameElement

    override fun getName(): String? {
        if (this is CrPathBasedDefinition) return nameElement?.name
        return super.getName()
    }

    override fun setName(name: String): PsiElement {
        throw UnsupportedOperationException()
    }

    override fun getTextOffset() = nameIdentifier?.textOffset ?: super.getTextOffset()

    override fun getPresentation() = CrystalDefinitionPresentation(this)
}