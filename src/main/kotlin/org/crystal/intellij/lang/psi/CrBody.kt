package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.SyntaxTraverser
import com.intellij.util.containers.JBIterable

class CrBody(node: ASTNode) : CrElementImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitBody(this)

    val expressions: JBIterable<CrExpression>
        get() = SyntaxTraverser
            .psiTraverser(this)
            .expand { it == this || it is CrCFieldGroup }
            .filter(CrExpression::class.java)
}