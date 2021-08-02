package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrEnum
import org.crystal.intellij.psi.CrVisibility
import org.crystal.intellij.stubs.api.CrEnumStub

class CrEnumStubImpl(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>,
    override val visibility: CrVisibility?
) : CrStubElementImpl<CrEnum>(parent, elementType), CrEnumStub {
    override fun toString() = "CrEnumStubImpl(visibility=$visibility)"
}