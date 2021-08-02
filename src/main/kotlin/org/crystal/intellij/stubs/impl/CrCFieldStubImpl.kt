package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrCField
import org.crystal.intellij.stubs.api.CrCFieldStub

class CrCFieldStubImpl(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>,
) : CrStubElementImpl<CrCField>(parent, elementType), CrCFieldStub