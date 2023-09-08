package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrFunction
import org.crystal.intellij.lang.stubs.api.CrFunctionStub
import org.crystal.intellij.lang.stubs.elementTypes.CrFunctionElementType

class CrFunctionStubImpl(
    parent: StubElement<*>?,
    override val isVariadic: Boolean
) : CrStubElementImpl<CrFunction>(parent, CrFunctionElementType), CrFunctionStub {
    override fun toString() = "CrFunctionStubImpl(isVariadic=$isVariadic)"
}