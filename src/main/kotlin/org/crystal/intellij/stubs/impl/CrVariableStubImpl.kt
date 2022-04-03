package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrVariable
import org.crystal.intellij.stubs.api.CrVariableStub
import org.crystal.intellij.stubs.elementTypes.CrVariableElementType

class CrVariableStubImpl(
    parent: StubElement<*>?,
    override val hasDefaultValue: Boolean
) : CrStubElementImpl<CrVariable>(parent, CrVariableElementType), CrVariableStub {
    override fun toString() = "CrVariableStubImpl(hasDefaultValue=$hasDefaultValue)"
}