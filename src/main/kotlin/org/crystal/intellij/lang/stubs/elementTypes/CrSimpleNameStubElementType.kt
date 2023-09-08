package org.crystal.intellij.lang.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.crystal.intellij.lang.psi.CrNameKind
import org.crystal.intellij.lang.psi.CrSimpleNameElement
import org.crystal.intellij.lang.stubs.api.CrNameStub
import org.crystal.intellij.lang.stubs.impl.CrFullNameStubImpl
import org.crystal.intellij.lang.stubs.impl.CrSingleNameStubImpl

object CrSimpleNameStubElementType : CrStubElementType<CrSimpleNameElement, CrNameStub<CrSimpleNameElement>>(
    "CR_SIMPLE_NAME_ELEMENT",
    ::CrSimpleNameElement,
    ::CrSimpleNameElement
) {
    override fun createStub(psi: CrSimpleNameElement, parentStub: StubElement<out PsiElement>?): CrNameStub<CrSimpleNameElement> {
        return if (psi.kind == CrNameKind.STRING)
            CrFullNameStubImpl(parentStub, psi.name, psi.sourceName)
        else
            CrSingleNameStubImpl(parentStub, psi.kind, psi.name)
    }

    override fun serialize(stub: CrNameStub<CrSimpleNameElement>, dataStream: StubOutputStream) {
        if (stub is CrFullNameStubImpl) {
            dataStream.writeBoolean(true)
            dataStream.writeName(stub.actualName)
            dataStream.writeName(stub.sourceName)
        }
        else {
            dataStream.writeBoolean(false)
            dataStream.writeVarInt(stub.kind.ordinal)
            dataStream.writeName(stub.actualName)
        }
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrNameStub<CrSimpleNameElement> {
        val isFull = dataStream.readBoolean()
        return if (isFull) {
            val actualName = dataStream.readNameString()
            val sourceName = dataStream.readNameString()
            CrFullNameStubImpl(parentStub, actualName, sourceName)
        } else {
            val kind = CrNameKind.of(dataStream.readVarInt())
            val name = dataStream.readNameString()
            CrSingleNameStubImpl(parentStub, kind, name)
        }
    }
}