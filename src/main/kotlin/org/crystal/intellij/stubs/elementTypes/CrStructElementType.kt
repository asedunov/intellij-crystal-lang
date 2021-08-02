package org.crystal.intellij.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.crystal.intellij.psi.CrStruct
import org.crystal.intellij.stubs.api.CrStructStub
import org.crystal.intellij.stubs.impl.CrStructStubImpl

object CrStructElementType : CrStubElementType<CrStruct, CrStructStub>(
    "CR_STRUCT_DEFINITION",
    ::CrStruct,
    ::CrStruct
) {
    override fun createStub(psi: CrStruct, parentStub: StubElement<out PsiElement>?): CrStructStub {
        return CrStructStubImpl(parentStub, this, psi.isAbstract, psi.visibility)
    }

    override fun serialize(stub: CrStructStub, dataStream: StubOutputStream) {
        dataStream.writeBoolean(stub.isAbstract)
        dataStream.writeVisibility(stub.visibility)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrStructStub {
        val isAbstract = dataStream.readBoolean()
        val visibility = dataStream.readVisibility()
        return CrStructStubImpl(parentStub, this, isAbstract, visibility)
    }
}