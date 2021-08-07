package org.crystal.intellij.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.crystal.intellij.psi.CrMethod
import org.crystal.intellij.stubs.api.CrMethodStub
import org.crystal.intellij.stubs.impl.CrMethodStubImpl
import org.crystal.intellij.stubs.indexes.indexFunction

object CrMethodElementType : CrStubElementType<CrMethod, CrMethodStub>(
    "CR_METHOD_DEFINITION",
    ::CrMethod,
    ::CrMethod
) {
    override fun createStub(psi: CrMethod, parentStub: StubElement<out PsiElement>?): CrMethodStub {
        return CrMethodStubImpl(parentStub, this, psi.isAbstract, psi.visibility)
    }

    override fun serialize(stub: CrMethodStub, dataStream: StubOutputStream) {
        dataStream.writeBoolean(stub.isAbstract)
        dataStream.writeVisibility(stub.visibility)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrMethodStub {
        val isAbstract = dataStream.readBoolean()
        val visibility = dataStream.readVisibility()
        return CrMethodStubImpl(parentStub, this, isAbstract, visibility)
    }

    override fun indexStub(stub: CrMethodStub, sink: IndexSink) {
        indexFunction(stub, sink)
    }
}