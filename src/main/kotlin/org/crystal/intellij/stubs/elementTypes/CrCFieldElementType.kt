package org.crystal.intellij.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import org.crystal.intellij.psi.CrCField
import org.crystal.intellij.stubs.api.CrCFieldStub
import org.crystal.intellij.stubs.impl.CrCFieldStubImpl

object CrCFieldElementType : CrStubElementType<CrCField, CrCFieldStub>(
    "CR_C_FIELD_DEFINITION",
    ::CrCField,
    ::CrCField
) {
    override fun createStub(psi: CrCField, parentStub: StubElement<out PsiElement>?): CrCFieldStub {
        return CrCFieldStubImpl(parentStub, this)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrCFieldStub {
        return CrCFieldStubImpl(parentStub, this)
    }
}