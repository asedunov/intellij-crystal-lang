package org.crystal.intellij.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.crystal.intellij.psi.CrVariable
import org.crystal.intellij.stubs.api.CrVariableStub
import org.crystal.intellij.stubs.impl.CrVariableStubImpl
import org.crystal.intellij.stubs.indexes.indexVariable

object CrVariableElementType : CrStubElementType<CrVariable, CrVariableStub>(
    "CR_VARIABLE_DECLARATION",
    ::CrVariable,
    ::CrVariable
) {
    override fun createStub(psi: CrVariable, parentStub: StubElement<out PsiElement>?): CrVariableStub {
        return CrVariableStubImpl(parentStub, psi.hasInitializer)
    }

    override fun serialize(stub: CrVariableStub, dataStream: StubOutputStream) {
        dataStream.writeBoolean(stub.hasInitializer)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrVariableStub {
        val hasDefaultValue = dataStream.readBoolean()
        return CrVariableStubImpl(parentStub, hasDefaultValue)
    }

    override fun indexStub(stub: CrVariableStub, sink: IndexSink) {
        indexVariable(stub, sink)
    }
}