package org.crystal.intellij.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.crystal.intellij.psi.CrMacro
import org.crystal.intellij.stubs.api.CrMacroStub
import org.crystal.intellij.stubs.impl.CrMacroStubImpl
import org.crystal.intellij.stubs.indexes.indexFunction

object CrMacroElementType : CrStubElementType<CrMacro, CrMacroStub>(
    "CR_MACRO_DEFINITION",
    ::CrMacro,
    ::CrMacro
) {
    override fun createStub(psi: CrMacro, parentStub: StubElement<out PsiElement>?): CrMacroStub {
        return CrMacroStubImpl(parentStub, this, psi.visibility)
    }

    override fun serialize(stub: CrMacroStub, dataStream: StubOutputStream) {
        dataStream.writeVisibility(stub.visibility)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrMacroStub {
        val visibility = dataStream.readVisibility()
        return CrMacroStubImpl(parentStub, this, visibility)
    }

    override fun indexStub(stub: CrMacroStub, sink: IndexSink) {
        indexFunction(stub, sink)
    }
}