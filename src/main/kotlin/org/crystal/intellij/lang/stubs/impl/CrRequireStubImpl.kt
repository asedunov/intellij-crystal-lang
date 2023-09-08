package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrRequireExpression
import org.crystal.intellij.lang.stubs.api.CrRequireStub
import org.crystal.intellij.lang.stubs.elementTypes.CrRequireElementType

class CrRequireStubImpl(
    parent: StubElement<*>?,
    override val filePath: String?
) : CrStubElementImpl<CrRequireExpression>(parent, CrRequireElementType), CrRequireStub {
    override fun toString() = "CrRequireStubImpl(filePath=$filePath)"
}