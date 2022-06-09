package org.crystal.intellij.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import org.crystal.intellij.psi.CrAnnotationExpression
import org.crystal.intellij.stubs.api.CrAnnotationExpressionStub
import org.crystal.intellij.stubs.impl.CrAnnotationExpressionStubImpl

object CrAnnotationExpressionElementType : CrStubElementType<CrAnnotationExpression, CrAnnotationExpressionStub>(
    "CR_ANNOTATION_EXPRESSION",
    ::CrAnnotationExpression,
    ::CrAnnotationExpression
) {
    override fun createStub(psi: CrAnnotationExpression, parentStub: StubElement<out PsiElement>?): CrAnnotationExpressionStub {
        return CrAnnotationExpressionStubImpl(parentStub)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrAnnotationExpressionStub {
        return CrAnnotationExpressionStubImpl(parentStub)
    }
}