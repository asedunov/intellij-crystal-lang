package org.crystal.intellij.psi

import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.util.elementType
import org.crystal.intellij.lexer.CR_PRIVATE
import org.crystal.intellij.lexer.CR_PROTECTED

interface CrDefinition : CrExpression, CrNameElementHolder, PsiNameIdentifierOwner {
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
            return when (parent) {
                is CrFile, is CrTypeBody, is CrLibrary -> CrVisibility.PUBLIC
                else -> null
            }
        }
}