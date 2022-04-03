package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrNameElement
import org.crystal.intellij.psi.CrNameKind
import org.crystal.intellij.stubs.api.CrNameStub
import org.crystal.intellij.stubs.elementTypes.CrSimpleNameStubElementType

class CrSingleNameStubImpl<Psi : CrNameElement>(
    parent: StubElement<*>?,
    override val kind: CrNameKind,
    val name: String?,
) : CrStubElementImpl<Psi>(parent, CrSimpleNameStubElementType), CrNameStub<Psi> {
    override val actualName: String?
        get() = name

    override val sourceName: String?
        get() = name

    override fun toString() = "CrSingleNameStubImpl(kind=$kind, name=$name)"
}