package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrSimpleParameter
import org.crystal.intellij.stubs.api.CrSimpleParameterStub
import org.crystal.intellij.stubs.elementTypes.CrSimpleParameterElementType

class CrSimpleParameterStubImpl(
    parent: StubElement<*>?,
    override val hasDefaultValue: Boolean
) : CrStubElementImpl<CrSimpleParameter>(parent, CrSimpleParameterElementType), CrSimpleParameterStub {
    override fun toString() = "CrSimpleParameterStubImpl(hasDefaultValue=$hasDefaultValue)"
}