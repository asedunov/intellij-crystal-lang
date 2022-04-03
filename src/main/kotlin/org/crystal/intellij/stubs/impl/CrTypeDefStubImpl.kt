package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrTypeDef
import org.crystal.intellij.stubs.api.CrTypeDefStub
import org.crystal.intellij.stubs.elementTypes.CrTypeDefElementType

class CrTypeDefStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrTypeDef>(parent, CrTypeDefElementType), CrTypeDefStub