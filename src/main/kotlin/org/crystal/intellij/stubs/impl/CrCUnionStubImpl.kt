package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrCUnion
import org.crystal.intellij.stubs.api.CrCUnionStub
import org.crystal.intellij.stubs.elementTypes.CrCUnionElementType

class CrCUnionStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrCUnion>(parent, CrCUnionElementType), CrCUnionStub