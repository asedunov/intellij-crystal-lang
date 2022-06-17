package org.crystal.intellij.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import org.crystal.intellij.psi.CrConstant
import org.crystal.intellij.stubs.api.CrConstantStub
import org.crystal.intellij.stubs.impl.CrConstantStubImpl

object CrConstantElementType : CrStubElementType<CrConstant, CrConstantStub>(
    "CR_CONSTANT_DEFINITION",
    ::CrConstant,
    ::CrConstant
) {
    override fun createStub(psi: CrConstant, parentStub: StubElement<out PsiElement>?): CrConstantStub {
        return CrConstantStubImpl(parentStub)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrConstantStub {
        return CrConstantStubImpl(parentStub)
    }
}