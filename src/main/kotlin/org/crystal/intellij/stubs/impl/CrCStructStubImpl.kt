package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrCStruct
import org.crystal.intellij.stubs.api.CrCStructStub

class CrCStructStubImpl(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>,
) : CrStubElementImpl<CrCStruct>(parent, elementType), CrCStructStub