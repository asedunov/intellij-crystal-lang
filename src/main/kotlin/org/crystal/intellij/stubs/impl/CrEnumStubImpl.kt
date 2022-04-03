package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrEnum
import org.crystal.intellij.psi.CrVisibility
import org.crystal.intellij.stubs.api.CrEnumStub
import org.crystal.intellij.stubs.elementTypes.CrEnumElementType

class CrEnumStubImpl(
    parent: StubElement<*>?,
    override val visibility: CrVisibility?
) : CrStubElementImpl<CrEnum>(parent, CrEnumElementType), CrEnumStub {
    override fun toString() = "CrEnumStubImpl(visibility=$visibility)"
}