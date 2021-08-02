package org.crystal.intellij.psi

import com.intellij.extapi.psi.StubBasedPsiElementBase
import org.crystal.intellij.stubs.api.CrStubWithDefaultValue

sealed interface CrDefinitionWithDefault : CrTypedDefinition {
    val hasDefaultValue: Boolean
        get() {
            if (this is StubBasedPsiElementBase<*>) {
                (greenStub as? CrStubWithDefaultValue)?.let { return it.hasDefaultValue }
            }
            return defaultValue != null
        }

    val defaultValue: CrExpression?
        get() = childOfType()
}