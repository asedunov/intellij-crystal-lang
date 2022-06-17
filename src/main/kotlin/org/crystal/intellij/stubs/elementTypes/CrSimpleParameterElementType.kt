package org.crystal.intellij.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.crystal.intellij.psi.CrSimpleParameter
import org.crystal.intellij.stubs.api.CrSimpleParameterStub
import org.crystal.intellij.stubs.impl.CrSimpleParameterStubImpl

object CrSimpleParameterElementType : CrStubElementType<CrSimpleParameter, CrSimpleParameterStub>(
    "CR_SIMPLE_PARAMETER_DEFINITION",
    ::CrSimpleParameter,
    ::CrSimpleParameter
) {
    override fun createStub(psi: CrSimpleParameter, parentStub: StubElement<out PsiElement>?): CrSimpleParameterStub {
        return CrSimpleParameterStubImpl(parentStub, psi.hasInitializer)
    }

    override fun serialize(stub: CrSimpleParameterStub, dataStream: StubOutputStream) {
        dataStream.writeBoolean(stub.hasInitializer)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrSimpleParameterStub {
        val hasDefaultValue = dataStream.readBoolean()
        return CrSimpleParameterStubImpl(parentStub, hasDefaultValue)
    }
}