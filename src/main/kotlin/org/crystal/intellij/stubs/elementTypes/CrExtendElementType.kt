package org.crystal.intellij.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import org.crystal.intellij.psi.CrExtendExpression
import org.crystal.intellij.stubs.api.CrExtendStub
import org.crystal.intellij.stubs.impl.CrExtendStubImpl

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
}