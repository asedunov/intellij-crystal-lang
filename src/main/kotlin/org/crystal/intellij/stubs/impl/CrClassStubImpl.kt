package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrClass
import org.crystal.intellij.psi.CrVisibility
import org.crystal.intellij.stubs.api.CrClassStub
import org.crystal.intellij.stubs.elementTypes.CrClassElementType

class CrClassStubImpl(
    parent: StubElement<*>?,
    override val isAbstract: Boolean,
    override val visibility: CrVisibility?
) : CrStubElementImpl<CrClass>(parent, CrClassElementType), CrClassStub {
    override fun toString() = "CrClassStubImpl(isAbstract=$isAbstract, visibility=$visibility)"
}