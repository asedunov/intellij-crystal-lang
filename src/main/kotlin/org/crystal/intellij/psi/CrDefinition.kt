package org.crystal.intellij.psi

import com.intellij.psi.PsiNameIdentifierOwner

interface CrDefinition : CrExpression, CrNameElementHolder, PsiNameIdentifierOwner {
    override fun accept(visitor: CrVisitor) = visitor.visitDefinition(this)
}