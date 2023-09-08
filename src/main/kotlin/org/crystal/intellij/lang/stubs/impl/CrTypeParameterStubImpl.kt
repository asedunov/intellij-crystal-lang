package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrTypeParameter
import org.crystal.intellij.lang.stubs.api.CrTypeParameterStub
import org.crystal.intellij.lang.stubs.elementTypes.CrTypeParameterElementType

class CrTypeParameterStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrTypeParameter>(parent, CrTypeParameterElementType), CrTypeParameterStub