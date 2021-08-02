package org.crystal.intellij.stubs.api

import org.crystal.intellij.psi.CrFunction

interface CrFunctionStub : CrDefinitionWithFqNameStub<CrFunction> {
    val isVariadic: Boolean
}