package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor

class CstTypeDef(
    val name: String,
    val typeSpec: CstNode,
    location: CstLocation? = null,
    override val nameLocation: CstLocation? = null
) : CstNode(location) {
    fun copy(
        name: String = this.name,
        typeSpec: CstNode = this.typeSpec,
        location: CstLocation? = this.location,
        nameLocation: CstLocation? = this.nameLocation
    ) = CstTypeDef(name, typeSpec, location, nameLocation)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstTypeDef

        if (name != other.name) return false
        if (typeSpec != other.typeSpec) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + typeSpec.hashCode()
        return result
    }

    override fun toString() = "TypeSpec($name, typeSpec=$typeSpec)"

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitTypeDef(this)

    override fun acceptChildren(visitor: CstVisitor) {
        typeSpec.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformTypeDef(this)
}