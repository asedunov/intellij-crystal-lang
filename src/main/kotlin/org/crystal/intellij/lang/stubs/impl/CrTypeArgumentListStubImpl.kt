package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrTypeArgumentList
import org.crystal.intellij.lang.stubs.api.CrTypeArgumentListStub
import org.crystal.intellij.lang.stubs.elementTypes.CrTypeArgumentListElementType

class CrTypeArgumentListStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrTypeArgumentList>(parent, CrTypeArgumentListElementType), CrTypeArgumentListStub