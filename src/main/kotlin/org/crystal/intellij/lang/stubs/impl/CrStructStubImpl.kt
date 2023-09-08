package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrStruct
import org.crystal.intellij.lang.psi.CrVisibility
import org.crystal.intellij.lang.stubs.api.CrStructStub
import org.crystal.intellij.lang.stubs.elementTypes.CrStructElementType

class CrStructStubImpl(
    parent: StubElement<*>?,
    override val isAbstract: Boolean,
    override val visibility: CrVisibility?
) : CrStubElementImpl<CrStruct>(parent, CrStructElementType), CrStructStub {
    override fun toString() = "CrStructStubImpl(isAbstract=$isAbstract, visibility=$visibility)"
}