package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrFunction
import org.crystal.intellij.stubs.api.CrFunctionStub
import org.crystal.intellij.stubs.elementTypes.CrFunctionElementType

class CrFunctionStubImpl(
    parent: StubElement<*>?,
    override val isVariadic: Boolean
) : CrStubElementImpl<CrFunction>(parent, CrFunctionElementType), CrFunctionStub {
    override fun toString() = "CrFunctionStubImpl(isVariadic=$isVariadic)"
}