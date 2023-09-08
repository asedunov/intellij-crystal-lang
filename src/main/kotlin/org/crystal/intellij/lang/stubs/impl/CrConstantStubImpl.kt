package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrConstant
import org.crystal.intellij.lang.psi.CrVisibility
import org.crystal.intellij.lang.stubs.api.CrConstantStub
import org.crystal.intellij.lang.stubs.elementTypes.CrConstantElementType

class CrConstantStubImpl(
    parent: StubElement<*>?,
    override val visibility: CrVisibility?
) : CrStubElementImpl<CrConstant>(parent, CrConstantElementType), CrConstantStub {
    override val hasInitializer: Boolean
        get() = true

    override fun toString() = "CrConstantStubImpl(visibility=$visibility)"
}