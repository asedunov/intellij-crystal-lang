package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrNameElement
import org.crystal.intellij.psi.CrNameKind
import org.crystal.intellij.stubs.api.CrNameStub
import org.crystal.intellij.stubs.elementTypes.CrSimpleNameStubElementType

class CrFullNameStubImpl<Psi : CrNameElement>(
    parent: StubElement<*>?,
    override val actualName: String?,
    override val sourceName: String?
) : CrStubElementImpl<Psi>(parent, CrSimpleNameStubElementType), CrNameStub<Psi> {
    override val kind: CrNameKind
        get() = CrNameKind.STRING

    override fun toString() = "CrFullNameStubImpl(actualName=$actualName, sourceName=$sourceName)"
}