package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrTypeParameter
import org.crystal.intellij.stubs.api.CrTypeParameterStub
import org.crystal.intellij.stubs.elementTypes.CrTypeParameterElementType

class CrTypeParameterStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrTypeParameter>(parent, CrTypeParameterElementType), CrTypeParameterStub