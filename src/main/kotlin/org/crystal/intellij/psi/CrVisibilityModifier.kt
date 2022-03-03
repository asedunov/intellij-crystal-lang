package org.crystal.intellij.psi

import com.intellij.psi.tree.IElementType
import org.crystal.intellij.lexer.CR_PRIVATE
import org.crystal.intellij.lexer.CR_PROTECTED

class CrVisibilityModifier(type: IElementType, text: CharSequence) : CrKeywordElement(type, text) {
    override fun accept(visitor: CrVisitor) = visitor.visitVisibilityModifier(this)

    val holder: CrVisibilityHolder?
        get() {
            val p = parent as? CrVisibilityHolder ?: return null
            return p.takeIf { it.visibilityModifier == this }
        }

    val visibility: CrVisibility?
        get() {
            return when (elementType) {
                CR_PRIVATE -> CrVisibility.PRIVATE
                CR_PROTECTED -> CrVisibility.PROTECTED
                else -> null
            }
        }
}