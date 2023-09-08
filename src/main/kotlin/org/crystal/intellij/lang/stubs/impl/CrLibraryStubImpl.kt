package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrLibrary
import org.crystal.intellij.lang.psi.CrVisibility
import org.crystal.intellij.lang.stubs.api.CrLibraryStub
import org.crystal.intellij.lang.stubs.elementTypes.CrLibraryElementType

class CrLibraryStubImpl(
    parent: StubElement<*>?,
    override val visibility: CrVisibility?
) : CrStubElementImpl<CrLibrary>(parent, CrLibraryElementType), CrLibraryStub {
    override fun toString() = "CrLibraryStubImpl(visibility=$visibility)"
}