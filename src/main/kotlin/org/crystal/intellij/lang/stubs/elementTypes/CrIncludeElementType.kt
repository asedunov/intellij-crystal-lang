package org.crystal.intellij.lang.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import org.crystal.intellij.lang.psi.CrIncludeExpression
import org.crystal.intellij.lang.stubs.api.CrIncludeStub
import org.crystal.intellij.lang.stubs.impl.CrIncludeStubImpl
import org.crystal.intellij.lang.stubs.indexes.indexIncludeLike

object CrIncludeElementType : CrStubElementType<CrIncludeExpression, CrIncludeStub>(
    "CR_INCLUDE_EXPRESSION",
    ::CrIncludeExpression,
    ::CrIncludeExpression
) {
    override fun createStub(psi: CrIncludeExpression, parentStub: StubElement<out PsiElement>?): CrIncludeStub {
        return CrIncludeStubImpl(parentStub)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrIncludeStub {
        return CrIncludeStubImpl(parentStub)
    }

    override fun indexStub(stub: CrIncludeStub, sink: IndexSink) {
        indexIncludeLike(stub, sink)
    }
}