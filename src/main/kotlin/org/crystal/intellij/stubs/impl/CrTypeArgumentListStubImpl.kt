package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrTypeArgumentList
import org.crystal.intellij.stubs.api.CrTypeArgumentListStub
import org.crystal.intellij.stubs.elementTypes.CrTypeArgumentListElementType

class CrTypeArgumentListStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrTypeArgumentList>(parent, CrTypeArgumentListElementType), CrTypeArgumentListStub