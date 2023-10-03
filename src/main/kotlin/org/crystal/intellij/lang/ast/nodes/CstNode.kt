package org.crystal.intellij.lang.ast.nodes

import com.intellij.openapi.util.text.StringUtil
import org.crystal.intellij.lang.ast.CstTransformer
import org.crystal.intellij.lang.ast.CstVisitor
import org.crystal.intellij.lang.ast.location.CstLocation

sealed class CstNode<T : CstNode<T>>(val location: CstLocation?) {
    abstract fun withLocation(location: CstLocation? = this.location): T

    open val nameLocation: CstLocation?
        get() = null

    protected val strippedClassName: String
        get() = StringUtil.trimStart(javaClass.simpleName, "Cst")

    fun accept(visitor: CstVisitor) {
        if (acceptSelf(visitor)) {
            acceptChildren(visitor)
        }
    }

    protected abstract fun acceptSelf(visitor: CstVisitor): Boolean

    protected open fun acceptChildren(visitor: CstVisitor) { }

    abstract fun acceptTransformer(transformer: CstTransformer): CstNode<*>
}