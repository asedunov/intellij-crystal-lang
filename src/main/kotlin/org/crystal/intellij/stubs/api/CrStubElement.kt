package org.crystal.intellij.stubs.api

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrElement

interface CrStubElement<T : CrElement> : StubElement<T>