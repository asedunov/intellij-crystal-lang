package org.crystal.intellij.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.crystal.intellij.psi.CrClass
import org.crystal.intellij.stubs.api.CrClassStub
import org.crystal.intellij.stubs.impl.CrClassStubImpl
import org.crystal.intellij.stubs.indexes.indexSuperclass
import org.crystal.intellij.stubs.indexes.indexType

object CrClassElementType : CrStubElementType<CrClass, CrClassStub>(
    "CR_CLASS_DEFINITION",
    ::CrClass,
    ::CrClass
) {
    override fun createStub(psi: CrClass, parentStub: StubElement<out PsiElement>?): CrClassStub {
        return CrClassStubImpl(parentStub, psi.isAbstract, psi.visibility)
    }

    override fun serialize(stub: CrClassStub, dataStream: StubOutputStream) {
        dataStream.writeBoolean(stub.isAbstract)
        dataStream.writeVisibility(stub.visibility)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrClassStub {
        val isAbstract = dataStream.readBoolean()
        val visibility = dataStream.readVisibility()
        return CrClassStubImpl(parentStub, isAbstract, visibility)
    }

    override fun indexStub(stub: CrClassStub, sink: IndexSink) {
        indexType(stub, sink)
        indexSuperclass(stub, sink)
    }
}