package org.crystal.intellij.lang.stubs.api

import org.crystal.intellij.lang.psi.CrParameterKind
import org.crystal.intellij.lang.psi.CrSimpleParameter

interface CrSimpleParameterStub : CrDefinitionStub<CrSimpleParameter>, CrStubWithInitializer<CrSimpleParameter> {
    val kind: CrParameterKind
}