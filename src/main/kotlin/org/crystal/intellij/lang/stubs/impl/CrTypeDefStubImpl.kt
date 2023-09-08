package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrTypeDef
import org.crystal.intellij.lang.stubs.api.CrTypeDefStub
import org.crystal.intellij.lang.stubs.elementTypes.CrTypeDefElementType

class CrTypeDefStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrTypeDef>(parent, CrTypeDefElementType), CrTypeDefStub