package org.crystal.intellij.lang.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.crystal.intellij.lang.psi.CrMacro
import org.crystal.intellij.lang.stubs.api.CrMacroStub
import org.crystal.intellij.lang.stubs.indexes.indexMacro
import org.crystal.intellij.lang.stubs.impl.CrMacroStubImpl

object CrMacroElementType : CrStubElementType<CrMacro, CrMacroStub>(
    "CR_MACRO_DEFINITION",
    ::CrMacro,
    ::CrMacro
) {
    override fun createStub(psi: CrMacro, parentStub: StubElement<out PsiElement>?): CrMacroStub {
        return CrMacroStubImpl(parentStub, psi.visibility)
    }

    override fun serialize(stub: CrMacroStub, dataStream: StubOutputStream) {
        dataStream.writeVisibility(stub.visibility)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrMacroStub {
        val visibility = dataStream.readVisibility()
        return CrMacroStubImpl(parentStub, visibility)
    }

    override fun indexStub(stub: CrMacroStub, sink: IndexSink) {
        indexMacro(stub, sink)
    }
}