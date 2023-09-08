package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrMacro
import org.crystal.intellij.lang.psi.CrVisibility
import org.crystal.intellij.lang.stubs.api.CrMacroStub
import org.crystal.intellij.lang.stubs.elementTypes.CrMacroElementType

class CrMacroStubImpl(
    parent: StubElement<*>?,
    override val visibility: CrVisibility?
) : CrStubElementImpl<CrMacro>(parent, CrMacroElementType), CrMacroStub {
    override fun toString() = "CrMacroStubImpl(visibility=$visibility)"
}