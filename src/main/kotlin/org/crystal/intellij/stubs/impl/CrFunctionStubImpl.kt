package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrFunction
import org.crystal.intellij.stubs.api.CrFunctionStub

class CrFunctionStubImpl(
    parent: StubElement<*>?,
    elementType: IStubElementType<out StubElement<*>, *>,
    override val isVariadic: Boolean
) : CrStubElementImpl<CrFunction>(parent, elementType), CrFunctionStub {
    override fun toString() = "CrFunctionStubImpl(isVariadic=$isVariadic)"
}