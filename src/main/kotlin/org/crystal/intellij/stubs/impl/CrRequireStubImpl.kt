package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrRequireExpression
import org.crystal.intellij.stubs.api.CrRequireStub

class CrRequireStubImpl(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>,
    override val filePath: String?
) : CrStubElementImpl<CrRequireExpression>(parent, elementType), CrRequireStub {
    override fun toString() = "CrRequireStubImpl(filePath=$filePath)"
}