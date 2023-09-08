package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrElement

open class CrStubElementImpl<T : CrElement>(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>
) : StubBase<T>(parent, elementType)