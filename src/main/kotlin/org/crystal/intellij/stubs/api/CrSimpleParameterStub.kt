package org.crystal.intellij.stubs.api

import org.crystal.intellij.psi.CrParameterKind
import org.crystal.intellij.psi.CrSimpleParameter

interface CrSimpleParameterStub : CrDefinitionStub<CrSimpleParameter>, CrStubWithInitializer<CrSimpleParameter> {
    val kind: CrParameterKind
}