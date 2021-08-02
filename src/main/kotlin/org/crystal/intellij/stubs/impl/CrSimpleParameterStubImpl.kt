package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrSimpleParameter
import org.crystal.intellij.stubs.api.CrSimpleParameterStub

class CrSimpleParameterStubImpl(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>,
    override val hasDefaultValue: Boolean
) : CrStubElementImpl<CrSimpleParameter>(parent, elementType), CrSimpleParameterStub {
    override fun toString() = "CrSimpleParameterStubImpl(hasDefaultValue=$hasDefaultValue)"
}