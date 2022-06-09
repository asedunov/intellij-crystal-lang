package org.crystal.intellij.psi

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.util.containers.JBIterable

sealed interface CrFunctionLikeDefinition : CrDefinitionWithFqName {
    val parameterList: CrParameterList?
        get() = childOfType()

    val parameters: JBIterable<CrSimpleParameter>
        get() {
            if (this is StubBasedPsiElementBase<*> && stub != null) {
                return stubChildrenOfType()
            }
            return parameterList?.elements ?: JBIterable.empty()
        }

    val returnType: CrType<*>?
        get() = childOfType()
}