package org.crystal.intellij.psi

sealed interface CrNameElement : CrReferenceElement {
    override fun accept(visitor: CrVisitor) = visitor.visitNameElement(this)

    val kind: CrNameKind

    abstract override fun getName(): String?

    fun setName(name: String): CrNameElement

    val sourceName: String?
}