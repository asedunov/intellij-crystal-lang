package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrTypeArgumentList
import org.crystal.intellij.stubs.api.CrTypeArgumentListStub

class CrTypeArgumentListStubImpl(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>
) : CrStubElementImpl<CrTypeArgumentList>(parent, elementType), CrTypeArgumentListStub