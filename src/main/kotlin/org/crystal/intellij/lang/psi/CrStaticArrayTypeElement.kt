package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.crystal.intellij.lang.lexer.CR_LBRACKET
import org.crystal.intellij.lang.parser.CR_STATIC_ARRAY_TYPE
import org.crystal.intellij.lang.stubs.api.CrTypeStub

class CrStaticArrayTypeElement : CrTypeElement<CrStaticArrayTypeElement> {
    constructor(stub: CrTypeStub<CrStaticArrayTypeElement>) : super(stub, CR_STATIC_ARRAY_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitStaticArrayType(this)

    val elementType: CrTypeElement<*>?
        get() = stubChildOfType()

    private val leftBracket: PsiElement?
        get() = findChildByType(CR_LBRACKET)

    val argument: CrElement?
        get() = leftBracket?.nextSiblingOfType()
}