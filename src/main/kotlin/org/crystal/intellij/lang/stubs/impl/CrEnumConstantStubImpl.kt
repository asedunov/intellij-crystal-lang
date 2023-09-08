package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrEnumConstant
import org.crystal.intellij.lang.stubs.api.CrEnumConstantStub
import org.crystal.intellij.lang.stubs.elementTypes.CrEnumConstantElementType

class CrEnumConstantStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrEnumConstant>(parent, CrEnumConstantElementType), CrEnumConstantStub