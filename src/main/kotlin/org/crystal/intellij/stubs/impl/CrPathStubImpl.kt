package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrPathNameElement
import org.crystal.intellij.stubs.api.CrPathStub

class CrPathStubImpl(
    parent: StubElement<*>?,
    override val name: String,
) : CrStubElementImpl<CrPathNameElement>(parent, CrPathNameStubElementType), CrPathStub {
    override fun toString() = "CrPathStubImpl(name=$name)"
}