package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrStruct
import org.crystal.intellij.psi.CrVisibility
import org.crystal.intellij.stubs.api.CrStructStub
import org.crystal.intellij.stubs.elementTypes.CrStructElementType

class CrStructStubImpl(
    parent: StubElement<*>?,
    override val isAbstract: Boolean,
    override val visibility: CrVisibility?
) : CrStubElementImpl<CrStruct>(parent, CrStructElementType), CrStructStub {
    override fun toString() = "CrStructStubImpl(isAbstract=$isAbstract, visibility=$visibility)"
}