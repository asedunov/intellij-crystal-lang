package org.crystal.intellij.lang.ast.nodes

import com.intellij.openapi.util.text.StringUtil
import org.crystal.intellij.lang.ast.location.CstLocation
import org.crystal.intellij.lang.ast.CstVisitor

sealed class CstNode(val location: CstLocation?) {
    protected val strippedClassName: String
        get() = StringUtil.trimStart(javaClass.simpleName, "Cst")

    fun accept(visitor: CstVisitor) {
        if (acceptSelf(visitor)) {
            acceptChildren(visitor)
        }
    }

    protected abstract fun acceptSelf(visitor: CstVisitor): Boolean

    protected open fun acceptChildren(visitor: CstVisitor) { }
}