package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrNameElement
import org.crystal.intellij.stubs.api.CrNameStub

class CrSingleNameStubImpl<Psi : CrNameElement>(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>,
    val name: String?,
) : CrStubElementImpl<Psi>(parent, elementType), CrNameStub<Psi> {
    override val actualName: String?
        get() = name

    override val sourceName: String?
        get() = name

    override fun toString() = "CrSingleNameStubImpl(name=$name)"
}