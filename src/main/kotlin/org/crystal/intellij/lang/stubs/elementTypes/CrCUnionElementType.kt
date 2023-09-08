package org.crystal.intellij.lang.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import org.crystal.intellij.lang.psi.CrCUnion
import org.crystal.intellij.lang.stubs.api.CrCUnionStub
import org.crystal.intellij.lang.stubs.impl.CrCUnionStubImpl
import org.crystal.intellij.lang.stubs.indexes.indexType

object CrCUnionElementType : CrStubElementType<CrCUnion, CrCUnionStub>(
    "CR_C_UNION_DEFINITION",
    ::CrCUnion,
    ::CrCUnion
) {
    override fun createStub(psi: CrCUnion, parentStub: StubElement<out PsiElement>?): CrCUnionStub {
        return CrCUnionStubImpl(parentStub)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrCUnionStub {
        return CrCUnionStubImpl(parentStub)
    }

    override fun indexStub(stub: CrCUnionStub, sink: IndexSink) = indexType(stub, sink)
}