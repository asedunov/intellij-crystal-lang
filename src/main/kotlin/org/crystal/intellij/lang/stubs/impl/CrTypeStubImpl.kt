package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrTypeElement
import org.crystal.intellij.lang.stubs.api.CrTypeStub

class CrTypeStubImpl<Psi : CrTypeElement<Psi>>(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>
) : CrStubElementImpl<Psi>(parent, elementType), CrTypeStub<Psi>