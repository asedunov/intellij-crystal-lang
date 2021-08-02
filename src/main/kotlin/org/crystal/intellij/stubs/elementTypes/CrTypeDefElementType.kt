package org.crystal.intellij.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import org.crystal.intellij.psi.CrTypeDef
import org.crystal.intellij.stubs.api.CrTypeDefStub
import org.crystal.intellij.stubs.impl.CrTypeDefStubImpl

object CrTypeDefElementType : CrStubElementType<CrTypeDef, CrTypeDefStub>(
    "CR_TYPE_DEFINITION",
    ::CrTypeDef,
    ::CrTypeDef
) {
    override fun createStub(psi: CrTypeDef, parentStub: StubElement<out PsiElement>?): CrTypeDefStub {
        return CrTypeDefStubImpl(parentStub, this)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrTypeDefStub {
        return CrTypeDefStubImpl(parentStub, this)
    }
}