package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.lang.lexer.CR_LBRACE
import org.crystal.intellij.lang.lexer.CR_OF

class CrHashExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitHashExpression(this)

    private val leftBrace: PsiElement
        get() = findChildByType(CR_LBRACE)!!

    val receiverType: CrTypeElement<*>?
        get() = leftBrace.prevSiblingOfType()

    val entries: JBIterable<CrHashEntry>
        get() = childrenOfType()

    private val ofKeyword: PsiElement?
        get() = findChildByType(CR_OF)

    val type: CrHashTypeElement?
        get() = ofKeyword?.nextSiblingOfType()
}