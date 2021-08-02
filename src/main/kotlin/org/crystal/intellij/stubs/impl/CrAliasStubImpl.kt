package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrAlias
import org.crystal.intellij.psi.CrVisibility
import org.crystal.intellij.stubs.api.CrAliasStub

class CrAliasStubImpl(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>,
    override val visibility: CrVisibility?
) : CrStubElementImpl<CrAlias>(parent, elementType), CrAliasStub {
    override fun toString() = "CrAliasStubImpl(visibility=$visibility)"
}