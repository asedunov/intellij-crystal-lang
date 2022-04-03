package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrMethod
import org.crystal.intellij.psi.CrVisibility
import org.crystal.intellij.stubs.api.CrMethodStub
import org.crystal.intellij.stubs.elementTypes.CrMethodElementType

class CrMethodStubImpl(
    parent: StubElement<*>?,
    override val isAbstract: Boolean,
    override val visibility: CrVisibility?
) : CrStubElementImpl<CrMethod>(parent, CrMethodElementType), CrMethodStub {
    override fun toString() = "CrMethodStubImpl(isAbstract=$isAbstract, visibility=$visibility)"
}