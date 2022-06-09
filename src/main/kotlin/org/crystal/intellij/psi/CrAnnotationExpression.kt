package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.resolve.symbols.CrSym

class CrAnnotationExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitAnnotationExpression(this)

    val path: CrPathNameElement?
        get() = childOfType()

    val targetSymbol: CrSym<*>?
        get() = path?.resolveSymbol()
}