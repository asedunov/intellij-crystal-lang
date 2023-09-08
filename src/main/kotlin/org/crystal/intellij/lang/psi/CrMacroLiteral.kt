package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable

class CrMacroLiteral(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitMacroLiteral(this)

    val elements: JBIterable<CrMacroLiteralElement>
        get() = childrenOfType()
}