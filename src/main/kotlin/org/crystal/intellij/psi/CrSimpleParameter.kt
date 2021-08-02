package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrSimpleParameter(node: ASTNode) : CrParameter(node), CrDefinitionWithDefault {
    override fun accept(visitor: CrVisitor) = visitor.visitSimpleParameter(this)

    val externalNameElement: CrSimpleNameElement?
        get() {
            val firstNameElement = childOfType<CrSimpleNameElement>()
            val secondNameElement = firstNameElement?.skipWhitespacesAndCommentsForward()
            return if (secondNameElement is CrSimpleNameElement) return firstNameElement else null
        }

    val externalName: String?
        get() = externalNameElement?.name

    override val nameElement: CrSimpleNameElement?
        get() {
            val externalNameElement = externalNameElement ?: return childOfType()
            return externalNameElement.nextSiblingOfType()
        }
}