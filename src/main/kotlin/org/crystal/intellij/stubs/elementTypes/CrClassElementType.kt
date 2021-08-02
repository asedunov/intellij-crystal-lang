package org.crystal.intellij.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.crystal.intellij.psi.CrClass
import org.crystal.intellij.stubs.api.CrClassStub
import org.crystal.intellij.stubs.impl.CrClassStubImpl

object CrClassElementType : CrStubElementType<CrClass, CrClassStub>(
    "CR_CLASS_DEFINITION",
    ::CrClass,
    ::CrClass
) {
    override fun createStub(psi: CrClass, parentStub: StubElement<out PsiElement>?): CrClassStub {
        return CrClassStubImpl(parentStub, this, psi.isAbstract, psi.visibility)
    }

    override fun serialize(stub: CrClassStub, dataStream: StubOutputStream) {
        dataStream.writeBoolean(stub.isAbstract)
        dataStream.writeVisibility(stub.visibility)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrClassStub {
        val isAbstract = dataStream.readBoolean()
        val visibility = dataStream.readVisibility()
        return CrClassStubImpl(parentStub, this, isAbstract, visibility)
    }
}