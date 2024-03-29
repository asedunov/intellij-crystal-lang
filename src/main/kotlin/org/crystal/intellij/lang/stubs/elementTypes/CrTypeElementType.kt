package org.crystal.intellij.lang.stubs.elementTypes

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import org.crystal.intellij.lang.psi.CrTypeElement
import org.crystal.intellij.lang.stubs.api.CrTypeStub
import org.crystal.intellij.lang.stubs.impl.CrTypeStubImpl

class CrTypeElementType<Psi : CrTypeElement<Psi>>(
    debugName: String,
    astPsiFactory: (ASTNode) -> Psi,
    stubPsiFactory: (CrTypeStub<Psi>) -> Psi
) : CrStubElementType<Psi, CrTypeStub<Psi>>(debugName, astPsiFactory, stubPsiFactory) {
    override fun createStub(psi: Psi, parentStub: StubElement<out PsiElement>?): CrTypeStub<Psi> {
        return CrTypeStubImpl(parentStub, this)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrTypeStub<Psi> {
        return CrTypeStubImpl(parentStub, this)
    }
}