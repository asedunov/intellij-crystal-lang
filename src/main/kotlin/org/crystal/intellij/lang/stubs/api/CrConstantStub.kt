package org.crystal.intellij.lang.stubs.api

import org.crystal.intellij.lang.psi.CrConstant

interface CrConstantStub :
    CrConstantLikeStub<CrConstant>,
    CrStubWithInitializer<CrConstant>