package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrType
import org.crystal.intellij.stubs.api.CrTypeStub

class CrTypeStubImpl<Psi : CrType<Psi>>(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>
) : CrStubElementImpl<Psi>(parent, elementType), CrTypeStub<Psi>