package org.crystal.intellij.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.crystal.intellij.parser.CR_STRING_LITERAL_EXPRESSION
import org.crystal.intellij.psi.CrSimpleNameElement
import org.crystal.intellij.stubs.api.CrNameStub
import org.crystal.intellij.stubs.impl.CrFullNameStubImpl
import org.crystal.intellij.stubs.impl.CrSingleNameStubImpl

object CrSimpleNameStubElementType : CrStubElementType<CrSimpleNameElement, CrNameStub<CrSimpleNameElement>>(
    "CR_SIMPLE_NAME_ELEMENT",
    ::CrSimpleNameElement,
    ::CrSimpleNameElement
) {
    override fun createStub(psi: CrSimpleNameElement, parentStub: StubElement<out PsiElement>?): CrNameStub<CrSimpleNameElement> {
        return if (psi.innerElementType == CR_STRING_LITERAL_EXPRESSION)
            CrFullNameStubImpl(parentStub, this, psi.name, psi.sourceName)
        else
            CrSingleNameStubImpl(parentStub, this, psi.name)
    }

    override fun serialize(stub: CrNameStub<CrSimpleNameElement>, dataStream: StubOutputStream) {
        if (stub is CrFullNameStubImpl) {
            dataStream.writeBoolean(true)
            dataStream.writeName(stub.actualName)
            dataStream.writeName(stub.sourceName)
        }
        else {
            dataStream.writeBoolean(false)
            dataStream.writeName(stub.actualName)
        }
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrNameStub<CrSimpleNameElement> {
        val isFull = dataStream.readBoolean()
        return if (isFull) {
            val actualName = dataStream.readNameString()
            val sourceName = dataStream.readNameString()
            CrFullNameStubImpl(parentStub, this, actualName, sourceName)
        } else {
            val name = dataStream.readNameString()
            CrSingleNameStubImpl(parentStub, this, name)
        }
    }
}