package org.crystal.intellij.lang.stubs.api

import org.crystal.intellij.lang.psi.CrElement

interface CrStubWithInitializer<T : CrElement> : CrStubElement<T> {
    val hasInitializer: Boolean
}