package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrClass
import org.crystal.intellij.lang.psi.CrVisibility
import org.crystal.intellij.lang.stubs.api.CrClassStub
import org.crystal.intellij.lang.stubs.elementTypes.CrClassElementType

class CrClassStubImpl(
    parent: StubElement<*>?,
    override val isAbstract: Boolean,
    override val visibility: CrVisibility?
) : CrStubElementImpl<CrClass>(parent, CrClassElementType), CrClassStub {
    override fun toString() = "CrClassStubImpl(isAbstract=$isAbstract, visibility=$visibility)"
}