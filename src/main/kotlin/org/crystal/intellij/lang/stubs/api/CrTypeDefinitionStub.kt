package org.crystal.intellij.lang.stubs.api

import org.crystal.intellij.lang.psi.CrTypeDefinition

interface CrTypeDefinitionStub<Psi : CrTypeDefinition> : CrConstantLikeStub<Psi>