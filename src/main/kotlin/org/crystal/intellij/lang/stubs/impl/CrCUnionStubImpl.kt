package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrCUnion
import org.crystal.intellij.lang.stubs.api.CrCUnionStub
import org.crystal.intellij.lang.stubs.elementTypes.CrCUnionElementType

class CrCUnionStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrCUnion>(parent, CrCUnionElementType), CrCUnionStub