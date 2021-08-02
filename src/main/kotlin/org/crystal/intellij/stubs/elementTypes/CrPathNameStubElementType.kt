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
        return CrPathStubImpl(parentStub, this, psi.isGlobal, psi.items.map { it.name ?: "" }.toList())
    }

    override fun serialize(stub: CrPathStub, dataStream: StubOutputStream) {
        dataStream.writeBoolean(stub.isGlobal)
        val items = stub.items
        dataStream.writeInt(items.size)
        items.forEach { dataStream.writeName(it) }
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrPathStub {
        val isGlobal = dataStream.readBoolean()
        val itemCount = dataStream.readInt()
        val items = Array(itemCount) { "" }
        for (i in 0 until itemCount) {
            items[i] = dataStream.readNameString() ?: ""
        }
        return CrPathStubImpl(parentStub, this, isGlobal, items.asList())
    }
}