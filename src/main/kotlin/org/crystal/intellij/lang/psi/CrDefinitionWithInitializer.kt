package org.crystal.intellij.lang.psi

import com.intellij.extapi.psi.StubBasedPsiElementBase
import org.crystal.intellij.lang.stubs.api.CrStubWithInitializer

sealed interface CrDefinitionWithInitializer : CrTypedDefinition {
    val hasInitializer: Boolean
        get() {
            if (this is StubBasedPsiElementBase<*>) {
                (greenStub as? CrStubWithInitializer)?.let { return it.hasInitializer }
            }
            return initializer != null
        }

    val initializer: CrExpression?
        get() = childOfType()
}