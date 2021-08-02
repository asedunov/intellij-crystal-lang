package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrMethod
import org.crystal.intellij.psi.CrVisibility
import org.crystal.intellij.stubs.api.CrMethodStub

class CrMethodStubImpl(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>,
    override val isAbstract: Boolean,
    override val visibility: CrVisibility?
) : CrStubElementImpl<CrMethod>(parent, elementType), CrMethodStub {
    override fun toString() = "CrMethodStubImpl(isAbstract=$isAbstract, visibility=$visibility)"
}