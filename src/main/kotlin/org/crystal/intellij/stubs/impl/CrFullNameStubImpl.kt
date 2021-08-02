package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrNameElement
import org.crystal.intellij.stubs.api.CrNameStub

class CrFullNameStubImpl<Psi : CrNameElement>(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>,
    override val actualName: String?,
    override val sourceName: String?
) : CrStubElementImpl<Psi>(parent, elementType), CrNameStub<Psi> {
    override fun toString() = "CrFullNameStubImpl(actualName=$actualName, sourceName=$sourceName)"
}