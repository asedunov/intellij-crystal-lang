package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrEnumConstant
import org.crystal.intellij.stubs.api.CrEnumConstantStub
import org.crystal.intellij.stubs.elementTypes.CrEnumConstantElementType

class CrEnumConstantStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrEnumConstant>(parent, CrEnumConstantElementType), CrEnumConstantStub