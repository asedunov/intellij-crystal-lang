package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrType
import org.crystal.intellij.stubs.api.CrTypeStub

class CrTypeStubImpl(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>
) : CrStubElementImpl<CrType>(parent, elementType), CrTypeStub