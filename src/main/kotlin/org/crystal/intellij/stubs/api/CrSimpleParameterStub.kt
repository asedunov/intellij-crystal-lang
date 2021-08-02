package org.crystal.intellij.stubs.api

import org.crystal.intellij.psi.CrSimpleParameter

interface CrSimpleParameterStub :
    CrDefinitionStub<CrSimpleParameter>,
    CrStubWithDefaultValue<CrSimpleParameter>