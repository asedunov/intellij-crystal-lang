package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrCStruct
import org.crystal.intellij.stubs.api.CrCStructStub
import org.crystal.intellij.stubs.elementTypes.CrCStructElementType

class CrCStructStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrCStruct>(parent, CrCStructElementType), CrCStructStub