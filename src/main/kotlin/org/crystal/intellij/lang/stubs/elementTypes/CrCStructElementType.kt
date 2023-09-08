package org.crystal.intellij.lang.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import org.crystal.intellij.lang.psi.CrCStruct
import org.crystal.intellij.lang.stubs.api.CrCStructStub
import org.crystal.intellij.lang.stubs.impl.CrCStructStubImpl
import org.crystal.intellij.lang.stubs.indexes.indexType

object CrCStructElementType : CrStubElementType<CrCStruct, CrCStructStub>(
    "CR_C_STRUCT_DEFINITION",
    ::CrCStruct,
    ::CrCStruct
) {
    override fun createStub(psi: CrCStruct, parentStub: StubElement<out PsiElement>?): CrCStructStub {
        return CrCStructStubImpl(parentStub)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrCStructStub {
        return CrCStructStubImpl(parentStub)
    }

    override fun indexStub(stub: CrCStructStub, sink: IndexSink) = indexType(stub, sink)
}