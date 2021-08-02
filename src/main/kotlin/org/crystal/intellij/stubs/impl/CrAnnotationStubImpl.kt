package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrAnnotation
import org.crystal.intellij.stubs.api.CrAnnotationStub

class CrAnnotationStubImpl(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>,
) : CrStubElementImpl<CrAnnotation>(parent, elementType), CrAnnotationStub