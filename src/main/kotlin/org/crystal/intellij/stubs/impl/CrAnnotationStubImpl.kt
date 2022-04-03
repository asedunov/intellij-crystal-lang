package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrAnnotation
import org.crystal.intellij.stubs.api.CrAnnotationStub
import org.crystal.intellij.stubs.elementTypes.CrAnnotationElementType

class CrAnnotationStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrAnnotation>(parent, CrAnnotationElementType), CrAnnotationStub