package org.crystal.intellij.lang.stubs.api

import org.crystal.intellij.lang.psi.CrFunction

interface CrFunctionStub : CrDefinitionWithFqNameStub<CrFunction> {
    val isVariadic: Boolean
}