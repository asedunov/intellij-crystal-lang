package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable

class CrAsmClobberList(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitAsmClobberList(this)

    val clobbers: JBIterable<CrStringLiteralExpression>
        get() = childrenOfType()
}