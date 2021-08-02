package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrEnumConstant
import org.crystal.intellij.stubs.api.CrEnumConstantStub

class CrEnumConstantStubImpl(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>,
) : CrStubElementImpl<CrEnumConstant>(parent, elementType), CrEnumConstantStub