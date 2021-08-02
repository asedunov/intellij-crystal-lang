package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrCUnion
import org.crystal.intellij.stubs.api.CrCUnionStub

class CrCUnionStubImpl(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>,
) : CrStubElementImpl<CrCUnion>(parent, elementType), CrCUnionStub