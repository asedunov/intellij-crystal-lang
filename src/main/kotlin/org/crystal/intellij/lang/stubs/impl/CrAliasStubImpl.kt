package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrAlias
import org.crystal.intellij.lang.psi.CrVisibility
import org.crystal.intellij.lang.stubs.api.CrAliasStub
import org.crystal.intellij.lang.stubs.elementTypes.CrAliasElementType

class CrAliasStubImpl(
    parent: StubElement<*>?,
    override val visibility: CrVisibility?
) : CrStubElementImpl<CrAlias>(parent, CrAliasElementType), CrAliasStub {
    override fun toString() = "CrAliasStubImpl(visibility=$visibility)"
}