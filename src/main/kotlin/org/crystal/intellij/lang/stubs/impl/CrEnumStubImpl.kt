package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrEnum
import org.crystal.intellij.lang.psi.CrVisibility
import org.crystal.intellij.lang.stubs.api.CrEnumStub
import org.crystal.intellij.lang.stubs.elementTypes.CrEnumElementType

class CrEnumStubImpl(
    parent: StubElement<*>?,
    override val visibility: CrVisibility?
) : CrStubElementImpl<CrEnum>(parent, CrEnumElementType), CrEnumStub {
    override fun toString() = "CrEnumStubImpl(visibility=$visibility)"
}