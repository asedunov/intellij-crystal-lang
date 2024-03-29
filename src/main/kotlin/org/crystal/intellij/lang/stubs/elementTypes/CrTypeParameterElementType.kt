package org.crystal.intellij.lang.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import org.crystal.intellij.lang.psi.CrTypeParameter
import org.crystal.intellij.lang.stubs.api.CrTypeParameterStub
import org.crystal.intellij.lang.stubs.impl.CrTypeParameterStubImpl

object CrTypeParameterElementType : CrStubElementType<CrTypeParameter, CrTypeParameterStub>(
    "CR_TYPE_PARAMETER_DEFINITION",
    ::CrTypeParameter,
    ::CrTypeParameter
) {
    override fun createStub(psi: CrTypeParameter, parentStub: StubElement<out PsiElement>?): CrTypeParameterStub {
        return CrTypeParameterStubImpl(parentStub)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrTypeParameterStub {
        return CrTypeParameterStubImpl(parentStub)
    }
}