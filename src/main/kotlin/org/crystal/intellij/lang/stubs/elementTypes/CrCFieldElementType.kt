package org.crystal.intellij.lang.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import org.crystal.intellij.lang.psi.CrCField
import org.crystal.intellij.lang.stubs.api.CrCFieldStub
import org.crystal.intellij.lang.stubs.impl.CrCFieldStubImpl
import org.crystal.intellij.lang.stubs.indexes.indexVariable

object CrCFieldElementType : CrStubElementType<CrCField, CrCFieldStub>(
    "CR_C_FIELD_DEFINITION",
    ::CrCField,
    ::CrCField
) {
    override fun createStub(psi: CrCField, parentStub: StubElement<out PsiElement>?): CrCFieldStub {
        return CrCFieldStubImpl(parentStub)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrCFieldStub {
        return CrCFieldStubImpl(parentStub)
    }

    override fun indexStub(stub: CrCFieldStub, sink: IndexSink) {
        indexVariable(stub, sink)
    }
}