package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubBase
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrElement

open class CrStubElementImpl<T : CrElement>(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>
) : StubBase<T>(parent, elementType)