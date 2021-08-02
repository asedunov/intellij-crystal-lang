package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrMacro
import org.crystal.intellij.psi.CrVisibility
import org.crystal.intellij.stubs.api.CrMacroStub

class CrMacroStubImpl(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>,
    override val visibility: CrVisibility?
) : CrStubElementImpl<CrMacro>(parent, elementType), CrMacroStub {
    override fun toString() = "CrMacroStubImpl(visibility=$visibility)"
}