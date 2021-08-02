package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrStruct
import org.crystal.intellij.psi.CrVisibility
import org.crystal.intellij.stubs.api.CrStructStub

class CrStructStubImpl(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>,
    override val isAbstract: Boolean,
    override val visibility: CrVisibility?
) : CrStubElementImpl<CrStruct>(parent, elementType), CrStructStub {
    override fun toString() = "CrStructStubImpl(isAbstract=$isAbstract, visibility=$visibility)"
}