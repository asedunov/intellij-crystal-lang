package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrCStruct
import org.crystal.intellij.lang.stubs.api.CrCStructStub
import org.crystal.intellij.lang.stubs.elementTypes.CrCStructElementType

class CrCStructStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrCStruct>(parent, CrCStructElementType), CrCStructStub