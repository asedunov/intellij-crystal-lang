package org.crystal.intellij.stubs.api

import org.crystal.intellij.psi.CrConstant

interface CrConstantStub :
    CrConstantLikeStub<CrConstant>,
    CrStubWithInitializer<CrConstant>