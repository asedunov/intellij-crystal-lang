package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.elementType
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.lexer.CR_PATH_OP

open class CrPath(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitPath(this)

    val isGlobal: Boolean
        get() = firstChild?.elementType == CR_PATH_OP

    val items: JBIterable<CrReferenceExpression>
        get() = childrenOfType()

    override fun getName() = items.lastOrNull()?.name
}