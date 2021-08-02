package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrLibrary
import org.crystal.intellij.psi.CrVisibility
import org.crystal.intellij.stubs.api.CrLibraryStub

class CrLibraryStubImpl(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>,
    override val visibility: CrVisibility?
) : CrStubElementImpl<CrLibrary>(parent, elementType), CrLibraryStub {
    override fun toString() = "CrLibraryStubImpl(visibility=$visibility)"
}