package org.crystal.intellij.stubs.elementTypes

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import org.crystal.intellij.psi.CrType
import org.crystal.intellij.stubs.api.CrTypeStub
import org.crystal.intellij.stubs.impl.CrTypeStubImpl

class CrTypeElementType<Psi : CrType>(
    debugName: String,
    astPsiFactory: (ASTNode) -> Psi,
    stubPsiFactory: (CrTypeStub) -> Psi
) : CrStubElementType<Psi, CrTypeStub>(debugName, astPsiFactory, stubPsiFactory) {
    override fun createStub(psi: Psi, parentStub: StubElement<out PsiElement>?): CrTypeStub {
        return CrTypeStubImpl(parentStub, this)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrTypeStub {
        return CrTypeStubImpl(parentStub, this)
    }
}