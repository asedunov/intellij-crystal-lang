package org.crystal.intellij.psi

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.StubBasedPsiElement
import com.intellij.psi.stubs.IStubElementType
import org.crystal.intellij.stubs.api.CrStubElement

abstract class CrStubbedElementImpl<T : CrStubElement<*>> : StubBasedPsiElementBase<T>, StubBasedPsiElement<T>, CrElement {
    constructor(stub: T, nodeType: IStubElementType<*, *>) : super(stub, nodeType)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: PsiElementVisitor) = acceptElement(visitor)

    override fun getName() = elementName

    override fun getTextOffset() = elementOffset

    override fun toString() = "${javaClass.simpleName}($elementType)"
}