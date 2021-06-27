package org.crystal.intellij.psi

import com.intellij.lang.ASTNode

class CrSimpleParameter(node: ASTNode) : CrParameter(node), CrDefinitionWithDefault {
    override fun accept(visitor: CrVisitor) = visitor.visitSimpleParameter(this)

    val externalNameElement: CrNameElement?
        get() {
            val firstNameElement = childOfType<CrNameElement>()
            val secondNameElement = firstNameElement?.skipWhitespacesAndCommentsForward()
            return if (secondNameElement is CrNameElement) return firstNameElement else null
        }

    override val nameElement: CrNameElement?
        get() {
            val externalNameElement = externalNameElement ?: return super<CrParameter>.nameElement
            return externalNameElement.nextSiblingOfType()
        }
}