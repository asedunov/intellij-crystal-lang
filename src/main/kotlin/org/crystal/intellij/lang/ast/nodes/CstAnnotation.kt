package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

class CstAnnotation(
    val path: CstPath,
    val args: List<CstNode<*>> = emptyList(),
    val namedArgs: List<CstNamedArgument> = emptyList(),
    location: CstLocation? = null
) : CstNode<CstAnnotation>(location) {
    fun copy(
        path: CstPath = this.path,
        args: List<CstNode<*>> = this.args,
        namedArgs: List<CstNamedArgument> = this.namedArgs,
        location: CstLocation? = this.location
    ) = CstAnnotation(path, args, namedArgs, location)

    override fun withLocation(location: CstLocation?) = copy(location = location)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CstAnnotation

        if (path != other.path) return false
        if (args != other.args) return false
        if (namedArgs != other.namedArgs) return false

        return true
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + args.hashCode()
        result = 31 * result + namedArgs.hashCode()
        return result
    }

    override fun toString() = buildString {
        append("Annotation(")
        append(path)
        if (args.isNotEmpty()) append(", args=$args")
        if (namedArgs.isNotEmpty()) append(", namedArgs=$namedArgs")
        append(")")
    }

    override fun acceptSelf(visitor: CstVisitor) = visitor.visitAnnotation(this)

    override fun acceptChildren(visitor: CstVisitor) {
        path.accept(visitor)
        args.forEach { it.accept(visitor) }
        namedArgs.forEach { it.accept(visitor) }
    }

    override fun acceptTransformer(transformer: CstTransformer) = transformer.transformAnnotation(this)
}