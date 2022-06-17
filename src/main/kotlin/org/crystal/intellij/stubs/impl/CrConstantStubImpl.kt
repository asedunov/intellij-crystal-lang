package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrConstant
import org.crystal.intellij.stubs.api.CrConstantStub
import org.crystal.intellij.stubs.elementTypes.CrConstantElementType

class CrConstantStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrConstant>(parent, CrConstantElementType), CrConstantStub {
    override val hasInitializer: Boolean
        get() = true

    override fun toString() = "CrConstantStubImpl(hasDefaultValue=$hasInitializer)"
}