package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstClassDef(
    val name: CstPath,
    val body: CstNode<*> = CstNop,
    val superclass: CstNode<*>? = null,
    val typeVars: List<String> = emptyList(),
    val isAbstract: Boolean = false,
    val isStruct: Boolean = false,
    val splatIndex: Int = -1,
    location: CstLocation? = null,
    override val nameLocation: CstLocation? = null
) : CstNode<CstClassDef>(location) {
    fun copy(
        name: CstPath = this.name,
        body: CstNode<*> = this.body,
        superclass: CstNode<*>? = this.superclass,
        typeVars: List<String> = this.typeVars,
        isAbstract: Boolean = this.isAbstract,
        isStruct: Boolean = this.isStruct,
        splatIndex: Int = this.splatIndex,
        location: CstLocation? = this.location,
        nameLocation: CstLocation? = this.nameLocation
    ) = CstClassDef(name, body, superclass, typeVars, isAbstract, isStruct, splatIndex, location, nameLocation)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstClassDef

        if (name != other.name) return false
        if (body != other.body) return false
        if (superclass != other.superclass) return false
        if (typeVars != other.typeVars) return false
        if (isAbstract != other.isAbstract) return false
        if (isStruct != other.isStruct) return false
        if (splatIndex != other.splatIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + body.hashCode()
        result = 31 * result + (superclass?.hashCode() ?: 0)
        result = 31 * result + typeVars.hashCode()
        result = 31 * result + isAbstract.hashCode()
        result = 31 * result + isStruct.hashCode()
        result = 31 * result + splatIndex
        return result
    }

    override fun toString() = buildString {
        append("ClassDef(")
        append(name)
        if (body != CstNop) append(", body=$body")
        if (superclass != null) append(", superclass=$superclass")
        if (typeVars.isNotEmpty()) append(", typeVars=$typeVars")
        if (isAbstract) append(", isAbstract")
        if (isStruct) append(", isStruct")
        if (splatIndex >= 0) append(", splatIndex=$splatIndex")
        append(")")
    }

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitClassDef(this)

    override fun acceptChildren(visitor: CstVisitor) {
        superclass?.accept(visitor)
        body.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformClassDef(this)
}