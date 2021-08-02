package org.crystal.intellij.stubs.api

import org.crystal.intellij.psi.CrDefinition
import org.crystal.intellij.psi.CrVisibility

interface CrDefinitionStub<Psi : CrDefinition> : CrStubElement<Psi> {
    val isAbstract: Boolean
        get() = false

    val visibility: CrVisibility?
        get() = null
}