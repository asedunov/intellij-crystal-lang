package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrLibrary
import org.crystal.intellij.psi.CrVisibility
import org.crystal.intellij.stubs.api.CrLibraryStub
import org.crystal.intellij.stubs.elementTypes.CrLibraryElementType

class CrLibraryStubImpl(
    parent: StubElement<*>?,
    override val visibility: CrVisibility?
) : CrStubElementImpl<CrLibrary>(parent, CrLibraryElementType), CrLibraryStub {
    override fun toString() = "CrLibraryStubImpl(visibility=$visibility)"
}