package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrRequireExpression
import org.crystal.intellij.stubs.api.CrRequireStub
import org.crystal.intellij.stubs.elementTypes.CrRequireElementType

class CrRequireStubImpl(
    parent: StubElement<*>?,
    override val filePath: String?
) : CrStubElementImpl<CrRequireExpression>(parent, CrRequireElementType), CrRequireStub {
    override fun toString() = "CrRequireStubImpl(filePath=$filePath)"
}