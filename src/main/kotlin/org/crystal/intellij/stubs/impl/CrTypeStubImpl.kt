package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrTypeElement
import org.crystal.intellij.stubs.api.CrTypeStub

class CrTypeStubImpl<Psi : CrTypeElement<Psi>>(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>
) : CrStubElementImpl<Psi>(parent, elementType), CrTypeStub<Psi>