package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrAnnotationExpression
import org.crystal.intellij.stubs.api.CrAnnotationExpressionStub
import org.crystal.intellij.stubs.elementTypes.CrAnnotationExpressionElementType

class CrAnnotationExpressionStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrAnnotationExpression>(parent, CrAnnotationExpressionElementType), CrAnnotationExpressionStub