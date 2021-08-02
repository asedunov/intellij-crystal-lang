package org.crystal.intellij.stubs.api

import org.crystal.intellij.psi.CrElement

interface CrStubWithDefaultValue<T : CrElement> : CrStubElement<T> {
    val hasDefaultValue: Boolean
}