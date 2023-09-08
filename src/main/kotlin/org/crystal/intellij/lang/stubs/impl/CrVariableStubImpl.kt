package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrVariable
import org.crystal.intellij.lang.stubs.api.CrVariableStub
import org.crystal.intellij.lang.stubs.elementTypes.CrVariableElementType

class CrVariableStubImpl(
    parent: StubElement<*>?,
    override val hasInitializer: Boolean
) : CrStubElementImpl<CrVariable>(parent, CrVariableElementType), CrVariableStub {
    override fun toString() = "CrVariableStubImpl(hasDefaultValue=$hasInitializer)"
}