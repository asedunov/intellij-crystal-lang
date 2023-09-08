package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrNameElement
import org.crystal.intellij.lang.psi.CrNameKind
import org.crystal.intellij.lang.stubs.api.CrNameStub
import org.crystal.intellij.lang.stubs.elementTypes.CrSimpleNameStubElementType

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