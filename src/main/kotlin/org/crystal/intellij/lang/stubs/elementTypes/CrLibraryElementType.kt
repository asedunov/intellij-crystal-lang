package org.crystal.intellij.lang.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.crystal.intellij.lang.psi.CrLibrary
import org.crystal.intellij.lang.stubs.api.CrLibraryStub
import org.crystal.intellij.lang.stubs.indexes.indexType
import org.crystal.intellij.lang.stubs.impl.CrLibraryStubImpl

object CrLibraryElementType : CrStubElementType<CrLibrary, CrLibraryStub>(
    "CR_LIBRARY_DEFINITION",
    ::CrLibrary,
    ::CrLibrary
) {
    override fun createStub(psi: CrLibrary, parentStub: StubElement<out PsiElement>?): CrLibraryStub {
        return CrLibraryStubImpl(parentStub, psi.visibility)
    }

    override fun serialize(stub: CrLibraryStub, dataStream: StubOutputStream) {
        dataStream.writeVisibility(stub.visibility)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrLibraryStub {
        val visibility = dataStream.readVisibility()
        return CrLibraryStubImpl(parentStub, visibility)
    }

    override fun indexStub(stub: CrLibraryStub, sink: IndexSink) {
        indexType(stub, sink)
    }
}