package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.crystal.intellij.lang.lexer.CR_PATH_OP

class CrFunctionPointerExpression(
    node: ASTNode
) : CrExpressionImpl(node), CrSimpleNameElementHolder {
    override fun accept(visitor: CrVisitor) = visitor.visitFunctionPointerExpression(this)

    val globalOp: PsiElement?
        get() = findChildByType(CR_PATH_OP)

    val isGlobal: Boolean
        get() = globalOp != null

    val receiver: CrMethodReceiver?
        get() = childOfType()

    val typeArgumentList: CrTypeArgumentList?
        get() = childOfType()
}