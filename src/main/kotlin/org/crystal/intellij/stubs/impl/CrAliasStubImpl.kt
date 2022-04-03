package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrAlias
import org.crystal.intellij.psi.CrVisibility
import org.crystal.intellij.stubs.api.CrAliasStub
import org.crystal.intellij.stubs.elementTypes.CrAliasElementType

class CrAliasStubImpl(
    parent: StubElement<*>?,
    override val visibility: CrVisibility?
) : CrStubElementImpl<CrAlias>(parent, CrAliasElementType), CrAliasStub {
    override fun toString() = "CrAliasStubImpl(visibility=$visibility)"
}