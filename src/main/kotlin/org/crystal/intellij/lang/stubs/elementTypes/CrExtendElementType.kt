package org.crystal.intellij.lang.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import org.crystal.intellij.lang.psi.CrExtendExpression
import org.crystal.intellij.lang.stubs.api.CrExtendStub
import org.crystal.intellij.lang.stubs.impl.CrExtendStubImpl
import org.crystal.intellij.lang.stubs.indexes.indexIncludeLike

object CrExtendElementType : CrStubElementType<CrExtendExpression, CrExtendStub>(
    "CR_EXTEND_EXPRESSION",
    ::CrExtendExpression,
    ::CrExtendExpression
) {
    override fun createStub(psi: CrExtendExpression, parentStub: StubElement<out PsiElement>?): CrExtendStub {
        return CrExtendStubImpl(parentStub)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrExtendStub {
        return CrExtendStubImpl(parentStub)
    }

    override fun indexStub(stub: CrExtendStub, sink: IndexSink) {
        indexIncludeLike(stub, sink)
    }
}