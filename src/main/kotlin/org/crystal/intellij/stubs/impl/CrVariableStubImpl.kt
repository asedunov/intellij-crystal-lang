package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrVariable
import org.crystal.intellij.stubs.api.CrVariableStub

class CrVariableStubImpl(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>,
    override val hasDefaultValue: Boolean
) : CrStubElementImpl<CrVariable>(parent, elementType), CrVariableStub {
    override fun toString() = "CrVariableStubImpl(hasDefaultValue=$hasDefaultValue)"
}