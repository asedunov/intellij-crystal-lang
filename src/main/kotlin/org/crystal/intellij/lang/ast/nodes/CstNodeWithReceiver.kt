package org.crystal.intellij.lang.ast.nodes

import org.crystal.intellij.lang.ast.location.CstLocation

sealed class CstNodeWithReceiver<T : CstNodeWithReceiver<T>>(
    location: CstLocation? = null
) : CstNode<T>(location) {
    abstract val obj: CstNode<*>?

    abstract fun withReceiver(obj: CstNode<*>? = this.obj): T
}