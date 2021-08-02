package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrClass
import org.crystal.intellij.psi.CrVisibility
import org.crystal.intellij.stubs.api.CrClassStub

class CrClassStubImpl(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>,
    override val isAbstract: Boolean,
    override val visibility: CrVisibility?
) : CrStubElementImpl<CrClass>(parent, elementType), CrClassStub {
    override fun toString() = "CrClassStubImpl(isAbstract=$isAbstract, visibility=$visibility)"
}