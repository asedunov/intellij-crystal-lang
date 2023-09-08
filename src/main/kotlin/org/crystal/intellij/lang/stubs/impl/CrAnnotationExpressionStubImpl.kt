package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrAnnotationExpression
import org.crystal.intellij.lang.stubs.api.CrAnnotationExpressionStub
import org.crystal.intellij.lang.stubs.elementTypes.CrAnnotationExpressionElementType

class CrAnnotationExpressionStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrAnnotationExpression>(parent, CrAnnotationExpressionElementType), CrAnnotationExpressionStub