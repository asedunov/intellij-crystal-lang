package org.crystal.intellij.stubs.api

import org.crystal.intellij.psi.CrElement

interface CrStubWithInitializer<T : CrElement> : CrStubElement<T> {
    val hasInitializer: Boolean
}