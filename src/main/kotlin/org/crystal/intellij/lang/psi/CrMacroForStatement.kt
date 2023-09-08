package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.lang.lexer.CR_IN

class CrMacroForStatement(node: ASTNode) : CrExpressionImpl(node), CrMacroLiteralElement {
    override fun accept(visitor: CrVisitor) = visitor.visitMacroForStatement(this)

    val variables: JBIterable<CrVariable>
        get() = childrenOfType()

    val iterable: CrExpression?
        get() = findChildByType<PsiElement>(CR_IN)?.nextSiblingOfType()

    val body: CrMacroLiteral?
        get() = childOfType()
}