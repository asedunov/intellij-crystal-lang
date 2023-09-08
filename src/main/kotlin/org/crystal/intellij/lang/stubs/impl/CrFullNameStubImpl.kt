package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrNameElement
import org.crystal.intellij.lang.psi.CrNameKind
import org.crystal.intellij.lang.stubs.api.CrNameStub
import org.crystal.intellij.lang.stubs.elementTypes.CrSimpleNameStubElementType

class CrFullNameStubImpl<Psi : CrNameElement>(
    parent: StubElement<*>?,
    override val actualName: String?,
    override val sourceName: String?
) : CrStubElementImpl<Psi>(parent, CrSimpleNameStubElementType), CrNameStub<Psi> {
    override val kind: CrNameKind
        get() = CrNameKind.STRING

    override fun toString() = "CrFullNameStubImpl(actualName=$actualName, sourceName=$sourceName)"
}