package org.crystal.intellij.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.crystal.intellij.lexer.CR_ABSTRACT
import org.crystal.intellij.lexer.CR_CONSTANT
import org.crystal.intellij.lexer.CR_PRIVATE
import org.crystal.intellij.lexer.CR_PROTECTED
import org.crystal.intellij.resolve.*

sealed interface CrDefinitionWithFqName : CrDefinition {
    val fqName: FqName?
        get() {
            if (this is CrPathBasedDefinition && nameElement?.isGlobal == true) return localFqName

            val parentFqName = when (val parent = getParentSkipModifiers()) {
                is CrFile -> null
                is CrBody -> (parent.parent as? CrBodyHolder)?.fqName as? StableFqName
                else -> return null
            }
            val nameElement = nameElement ?: return null
            return when (nameElement) {
                is CrPathNameElement -> nameElement.getLocalFqName(parentFqName)
                is CrSimpleNameElement -> {
                    val isConstant = nameElement.tokenType == CR_CONSTANT
                    val name = name ?: NO_NAME
                    return if (isConstant) StableFqName(name, parentFqName) else MemberFqName(name, parentFqName)
                }
            }
        }

    val isLocal: Boolean
        get() {
            val p = getParentSkipModifiers()
            return !(p is CrFile || p is CrBody)
        }

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
            return if (isLocal) null else CrVisibility.PUBLIC
        }

    val abstractModifier: PsiElement?
        get() = allChildren().firstOrNull { it.elementType == CR_ABSTRACT }

    val isAbstract: Boolean
        get() = abstractModifier != null
}