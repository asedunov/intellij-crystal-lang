package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrModule
import org.crystal.intellij.lang.psi.CrVisibility
import org.crystal.intellij.lang.stubs.api.CrModuleStub
import org.crystal.intellij.lang.stubs.elementTypes.CrModuleElementType

class CrModuleStubImpl(
    parent: StubElement<*>?,
    override val visibility: CrVisibility?
) : CrStubElementImpl<CrModule>(parent, CrModuleElementType), CrModuleStub {
    override fun toString() = "CrModuleStubImpl(visibility=$visibility)"
}