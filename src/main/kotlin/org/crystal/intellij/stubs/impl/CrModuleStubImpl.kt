package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrModule
import org.crystal.intellij.psi.CrVisibility
import org.crystal.intellij.stubs.api.CrModuleStub

class CrModuleStubImpl(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>,
    override val visibility: CrVisibility?
) : CrStubElementImpl<CrModule>(parent, elementType), CrModuleStub {
    override fun toString() = "CrModuleStubImpl(visibility=$visibility)"
}