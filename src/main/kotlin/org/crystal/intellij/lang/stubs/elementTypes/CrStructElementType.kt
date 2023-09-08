package org.crystal.intellij.lang.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.crystal.intellij.lang.psi.CrStruct
import org.crystal.intellij.lang.stubs.api.CrStructStub
import org.crystal.intellij.lang.stubs.impl.CrStructStubImpl
import org.crystal.intellij.lang.stubs.indexes.indexSuperclass
import org.crystal.intellij.lang.stubs.indexes.indexType

object CrStructElementType : CrStubElementType<CrStruct, CrStructStub>(
    "CR_STRUCT_DEFINITION",
    ::CrStruct,
    ::CrStruct
) {
    override fun createStub(psi: CrStruct, parentStub: StubElement<out PsiElement>?): CrStructStub {
        return CrStructStubImpl(parentStub, psi.isAbstract, psi.visibility)
    }

    override fun serialize(stub: CrStructStub, dataStream: StubOutputStream) {
        dataStream.writeBoolean(stub.isAbstract)
        dataStream.writeVisibility(stub.visibility)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrStructStub {
        val isAbstract = dataStream.readBoolean()
        val visibility = dataStream.readVisibility()
        return CrStructStubImpl(parentStub, isAbstract, visibility)
    }

    override fun indexStub(stub: CrStructStub, sink: IndexSink) {
        indexType(stub, sink)
        indexSuperclass(stub, sink)
    }
}