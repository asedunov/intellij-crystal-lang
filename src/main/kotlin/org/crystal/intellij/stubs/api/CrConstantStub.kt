package org.crystal.intellij.stubs.api

import org.crystal.intellij.psi.CrConstant

interface CrConstantStub :
    CrDefinitionWithFqNameStub<CrConstant>,
    CrStubWithInitializer<CrConstant>