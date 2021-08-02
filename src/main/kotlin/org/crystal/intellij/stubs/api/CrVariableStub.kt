package org.crystal.intellij.stubs.api

import org.crystal.intellij.psi.CrVariable

interface CrVariableStub :
    CrDefinitionWithFqNameStub<CrVariable>,
    CrStubWithDefaultValue<CrVariable>