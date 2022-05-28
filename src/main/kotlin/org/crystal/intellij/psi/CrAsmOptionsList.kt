package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable

class CrAsmOptionsList(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitAsmOptionsList(this)

    val options: JBIterable<CrStringLiteralExpression>
        get() = childrenOfType()
}