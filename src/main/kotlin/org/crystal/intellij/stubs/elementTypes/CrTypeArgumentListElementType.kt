package org.crystal.intellij.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import org.crystal.intellij.psi.CrTypeArgumentList
import org.crystal.intellij.stubs.api.CrTypeArgumentListStub
import org.crystal.intellij.stubs.impl.CrTypeArgumentListStubImpl

object CrTypeArgumentListElementType : CrStubElementType<CrTypeArgumentList, CrTypeArgumentListStub>(
    "CR_TYPE_ARGUMENT_LIST",
    ::CrTypeArgumentList,
    ::CrTypeArgumentList
) {
    override fun createStub(psi: CrTypeArgumentList, parentStub: StubElement<out PsiElement>?): CrTypeArgumentListStub {
        return CrTypeArgumentListStubImpl(parentStub, this)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrTypeArgumentListStub {
        return CrTypeArgumentListStubImpl(parentStub, this)
    }
}