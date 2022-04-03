package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrMacro
import org.crystal.intellij.psi.CrVisibility
import org.crystal.intellij.stubs.api.CrMacroStub
import org.crystal.intellij.stubs.elementTypes.CrMacroElementType

class CrMacroStubImpl(
    parent: StubElement<*>?,
    override val visibility: CrVisibility?
) : CrStubElementImpl<CrMacro>(parent, CrMacroElementType), CrMacroStub {
    override fun toString() = "CrMacroStubImpl(visibility=$visibility)"
}