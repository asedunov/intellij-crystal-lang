package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrAnnotation
import org.crystal.intellij.lang.stubs.api.CrAnnotationStub
import org.crystal.intellij.lang.stubs.elementTypes.CrAnnotationElementType

class CrAnnotationStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrAnnotation>(parent, CrAnnotationElementType), CrAnnotationStub