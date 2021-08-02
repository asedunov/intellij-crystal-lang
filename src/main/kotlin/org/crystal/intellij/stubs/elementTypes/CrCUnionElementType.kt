package org.crystal.intellij.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import org.crystal.intellij.psi.CrCUnion
import org.crystal.intellij.stubs.api.CrCUnionStub
import org.crystal.intellij.stubs.impl.CrCUnionStubImpl

object CrCUnionElementType : CrStubElementType<CrCUnion, CrCUnionStub>(
    "CR_C_UNION_DEFINITION",
    ::CrCUnion,
    ::CrCUnion
) {
    override fun createStub(psi: CrCUnion, parentStub: StubElement<out PsiElement>?): CrCUnionStub {
        return CrCUnionStubImpl(parentStub, this)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrCUnionStub {
        return CrCUnionStubImpl(parentStub, this)
    }
}