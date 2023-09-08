package org.crystal.intellij.lang.stubs.api

import org.crystal.intellij.lang.psi.CrVariable

interface CrVariableStub :
    CrDefinitionWithFqNameStub<CrVariable>,
    CrStubWithInitializer<CrVariable>