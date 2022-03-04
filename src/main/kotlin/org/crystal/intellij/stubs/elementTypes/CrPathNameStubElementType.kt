package org.crystal.intellij.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.crystal.intellij.psi.CrPathNameElement
import org.crystal.intellij.stubs.api.CrPathStub
import org.crystal.intellij.stubs.impl.CrPathStubImpl

object CrPathNameStubElementType : CrStubElementType<CrPathNameElement, CrPathStub>(
    "CR_PATH_NAME_ELEMENT",
    ::CrPathNameElement,
    ::CrPathNameElement
) {
    override fun createStub(psi: CrPathNameElement, parentStub: StubElement<out PsiElement>?): CrPathStub {
        return CrPathStubImpl(parentStub, this, psi.name)
    }

    override fun serialize(stub: CrPathStub, dataStream: StubOutputStream) {
        dataStream.writeName(stub.name)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrPathStub {
        val name = dataStream.readNameString() ?: ""
        return CrPathStubImpl(parentStub, this, name)
    }
}