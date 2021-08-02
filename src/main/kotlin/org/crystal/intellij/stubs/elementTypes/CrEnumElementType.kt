package org.crystal.intellij.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.crystal.intellij.psi.CrEnum
import org.crystal.intellij.stubs.api.CrEnumStub
import org.crystal.intellij.stubs.impl.CrEnumStubImpl

object CrEnumElementType : CrStubElementType<CrEnum, CrEnumStub>(
    "CR_ENUM_DEFINITION",
    ::CrEnum,
    ::CrEnum
) {
    override fun createStub(psi: CrEnum, parentStub: StubElement<out PsiElement>?): CrEnumStub {
        return CrEnumStubImpl(parentStub, this, psi.visibility)
    }

    override fun serialize(stub: CrEnumStub, dataStream: StubOutputStream) {
        dataStream.writeVisibility(stub.visibility)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrEnumStub {
        val visibility = dataStream.readVisibility()
        return CrEnumStubImpl(parentStub, this, visibility)
    }
}