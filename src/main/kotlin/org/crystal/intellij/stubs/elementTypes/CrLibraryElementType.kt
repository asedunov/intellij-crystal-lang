package org.crystal.intellij.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.crystal.intellij.psi.CrLibrary
import org.crystal.intellij.stubs.api.CrLibraryStub
import org.crystal.intellij.stubs.impl.CrLibraryStubImpl
import org.crystal.intellij.stubs.indexes.indexType

object CrLibraryElementType : CrStubElementType<CrLibrary, CrLibraryStub>(
    "CR_LIBRARY_DEFINITION",
    ::CrLibrary,
    ::CrLibrary
) {
    override fun createStub(psi: CrLibrary, parentStub: StubElement<out PsiElement>?): CrLibraryStub {
        return CrLibraryStubImpl(parentStub, this, psi.visibility)
    }

    override fun serialize(stub: CrLibraryStub, dataStream: StubOutputStream) {
        dataStream.writeVisibility(stub.visibility)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrLibraryStub {
        val visibility = dataStream.readVisibility()
        return CrLibraryStubImpl(parentStub, this, visibility)
    }

    override fun indexStub(stub: CrLibraryStub, sink: IndexSink) {
        indexType(stub, sink)
    }
}