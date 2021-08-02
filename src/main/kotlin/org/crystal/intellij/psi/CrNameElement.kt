package org.crystal.intellij.psi

sealed interface CrNameElement : CrElement {
    override fun accept(visitor: CrVisitor) = visitor.visitNameElement(this)

    abstract override fun getName(): String?

    val sourceName: String?
}