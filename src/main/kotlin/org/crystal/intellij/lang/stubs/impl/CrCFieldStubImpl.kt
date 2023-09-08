package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrCField
import org.crystal.intellij.lang.stubs.api.CrCFieldStub
import org.crystal.intellij.lang.stubs.elementTypes.CrCFieldElementType

class CrCFieldStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrCField>(parent, CrCFieldElementType), CrCFieldStub