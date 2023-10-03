package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstDef(
    val name: String,
    val args: List<CstArg> = emptyList(),
    val body: CstNode<*> = CstNop,
    val receiver: CstNode<*>? = null,
    val blockArg: CstArg? = null,
    val returnType: CstNode<*>? = null,
    val isAbstract: Boolean = false,
    val blockArity: Int = -1,
    val splatIndex: Int = -1,
    val doubleSplat: CstArg? = null,
    val freeVars: List<String> = emptyList(),
    val isMacroDef: Boolean = false,
    location: CstLocation? = null,
    override val nameLocation: CstLocation? = null
) : CstNode<CstDef>(location) {
    fun copy(
        name: String = this.name,
        args: List<CstArg> = this.args,
        body: CstNode<*> = this.body,
        receiver: CstNode<*>? = this.receiver,
        blockArg: CstArg? = this.blockArg,
        returnType: CstNode<*>? = this.returnType,
        isAbstract: Boolean = this.isAbstract,
        blockArity: Int = this.blockArity,
        splatIndex: Int = this.splatIndex,
        doubleSplat: CstArg? = this.doubleSplat,
        freeVars: List<String> = this.freeVars,
        isMacroDef: Boolean = this.isMacroDef,
        location: CstLocation? = this.location,
        nameLocation: CstLocation? = this.nameLocation
    ) = CstDef(
        name,
        args,
        body,
        receiver,
        blockArg,
        returnType,
        isAbstract,
        blockArity,
        splatIndex,
        doubleSplat,
        freeVars,
        isMacroDef,
        location,
        nameLocation
    )

    override fun withLocation(location: CstLocation?) = copy(location = location)
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstDef

        if (name != other.name) return false
        if (args != other.args) return false
        if (body != other.body) return false
        if (receiver != other.receiver) return false
        if (blockArg != other.blockArg) return false
        if (returnType != other.returnType) return false
        if (isAbstract != other.isAbstract) return false
        if (blockArity != other.blockArity) return false
        if (splatIndex != other.splatIndex) return false
        if (doubleSplat != other.doubleSplat) return false
        if (isMacroDef != other.isMacroDef) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + args.hashCode()
        result = 31 * result + body.hashCode()
        result = 31 * result + (receiver?.hashCode() ?: 0)
        result = 31 * result + (blockArg?.hashCode() ?: 0)
        result = 31 * result + (returnType?.hashCode() ?: 0)
        result = 31 * result + isAbstract.hashCode()
        result = 31 * result + blockArity
        result = 31 * result + splatIndex
        result = 31 * result + (doubleSplat?.hashCode() ?: 0)
        result = 31 * result + isMacroDef.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("Def(")
        append(name)
        if (receiver != null) append(", receiver=$receiver, ")
        if (args.isNotEmpty()) append(", args=$args")
        if (body != CstNop) append(", body=$body")
        if (blockArg != null) append(", blockArg=$blockArg")
        if (returnType != null) append(", returnType=$returnType")
        if (isAbstract) append(", isAbstract")
        if (blockArity >= 0) append(", blockArity=$blockArity")
        if (splatIndex >= 0) append(", splatIndex=$splatIndex")
        if (doubleSplat != null) append(", doubleSplat=$doubleSplat")
        if (freeVars.isNotEmpty()) append(", freeVars=$freeVars")
        if (isMacroDef) append(", isMacroDef")
        append(")")
    }

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitDef(this)

    override fun acceptChildren(visitor: CstVisitor) {
        receiver?.accept(visitor)
        args.forEach { it.accept(visitor) }
        doubleSplat?.accept(visitor)
        blockArg?.accept(visitor)
        returnType?.accept(visitor)
        body.accept(visitor)
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformDef(this)
}