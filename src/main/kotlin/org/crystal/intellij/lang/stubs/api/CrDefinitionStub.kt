package org.crystal.intellij.lang.stubs.api

import org.crystal.intellij.lang.psi.CrDefinition
import org.crystal.intellij.lang.psi.CrVisibility

interface CrDefinitionStub<Psi : CrDefinition> : CrStubElement<Psi> {
    val isAbstract: Boolean
        get() = false

    val visibility: CrVisibility?
        get() = null
}