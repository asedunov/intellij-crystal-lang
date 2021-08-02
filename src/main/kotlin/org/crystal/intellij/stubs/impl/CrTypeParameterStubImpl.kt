package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrTypeParameter
import org.crystal.intellij.stubs.api.CrTypeParameterStub

class CrTypeParameterStubImpl(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>
) : CrStubElementImpl<CrTypeParameter>(parent, elementType), CrTypeParameterStub