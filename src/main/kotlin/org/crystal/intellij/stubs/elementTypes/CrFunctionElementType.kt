package org.crystal.intellij.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.crystal.intellij.psi.CrFunction
import org.crystal.intellij.stubs.api.CrFunctionStub
import org.crystal.intellij.stubs.impl.CrFunctionStubImpl
import org.crystal.intellij.stubs.indexes.indexFunction

object CrFunctionElementType : CrStubElementType<CrFunction, CrFunctionStub>(
    "CR_FUNCTION_DEFINITION",
    ::CrFunction,
    ::CrFunction
) {
    override fun createStub(psi: CrFunction, parentStub: StubElement<out PsiElement>?): CrFunctionStub {
        return CrFunctionStubImpl(parentStub, this, psi.isVariadic)
    }

    override fun serialize(stub: CrFunctionStub, dataStream: StubOutputStream) {
        dataStream.writeBoolean(stub.isVariadic)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrFunctionStub {
        val isVariadic = dataStream.readBoolean()
        return CrFunctionStubImpl(parentStub, this, isVariadic)
    }

    override fun indexStub(stub: CrFunctionStub, sink: IndexSink) {
        indexFunction(stub, sink)
    }
}