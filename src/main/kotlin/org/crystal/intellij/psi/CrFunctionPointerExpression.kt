package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.crystal.intellij.lexer.CR_PATH_OP

class CrFunctionPointerExpression(node: ASTNode) : CrExpressionImpl(node) {
    override fun accept(visitor: CrVisitor) = visitor.visitFunctionPointerExpression(this)

    val globalOp: PsiElement?
        get() = findChildByType(CR_PATH_OP)

    val receiver: CrMethodReceiver?
        get() = childOfType()
}