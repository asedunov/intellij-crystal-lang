package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrModule
import org.crystal.intellij.psi.CrVisibility
import org.crystal.intellij.stubs.api.CrModuleStub
import org.crystal.intellij.stubs.elementTypes.CrModuleElementType

class CrModuleStubImpl(
    parent: StubElement<*>?,
    override val visibility: CrVisibility?
) : CrStubElementImpl<CrModule>(parent, CrModuleElementType), CrModuleStub {
    override fun toString() = "CrModuleStubImpl(visibility=$visibility)"
}