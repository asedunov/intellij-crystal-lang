package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrCField
import org.crystal.intellij.stubs.api.CrCFieldStub
import org.crystal.intellij.stubs.elementTypes.CrCFieldElementType

class CrCFieldStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrCField>(parent, CrCFieldElementType), CrCFieldStub