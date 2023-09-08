package org.crystal.intellij.lang.stubs.api

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrElement

interface CrStubElement<T : CrElement> : StubElement<T>