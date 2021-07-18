package org.crystal.intellij.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.crystal.intellij.lexer.CR_ABSTRACT
import org.crystal.intellij.lexer.CR_PRIVATE
import org.crystal.intellij.lexer.CR_PROTECTED

sealed interface CrDefinition : CrExpression, CrNamedElement {
    override fun accept(visitor: CrVisitor) = visitor.visitDefinition(this)

    val visibility: CrVisibility?
        get() {
            val visibility = (parent as? CrVisibilityExpression)?.visibility
            if (visibility != null) return visibility
            for (child in allChildren()) {
                when (child.elementType) {
                    CR_PRIVATE -> return CrVisibility.PRIVATE
                    CR_PROTECTED -> return CrVisibility.PROTECTED
                }
            }
            var parent = parent
            if (parent is CrBody) parent = parent.parent
            return when (parent) {
                is CrFile, is CrTypeDefinition, is CrLibrary -> CrVisibility.PUBLIC
                else -> null
            }
        }

    val abstractModifier: PsiElement?
        get() = allChildren().firstOrNull { it.elementType == CR_ABSTRACT }

    val isAbstract: Boolean
        get() = abstractModifier != null
}