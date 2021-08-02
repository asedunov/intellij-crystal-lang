package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrTypeDef
import org.crystal.intellij.stubs.api.CrTypeDefStub

class CrTypeDefStubImpl(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>,
) : CrStubElementImpl<CrTypeDef>(parent, elementType), CrTypeDefStub