package org.crystal.intellij.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.crystal.intellij.psi.CrModule
import org.crystal.intellij.stubs.api.CrModuleStub
import org.crystal.intellij.stubs.impl.CrModuleStubImpl
import org.crystal.intellij.stubs.indexes.indexType

object CrModuleElementType : CrStubElementType<CrModule, CrModuleStub>(
    "CR_MODULE_DEFINITION",
    ::CrModule,
    ::CrModule
) {
    override fun createStub(psi: CrModule, parentStub: StubElement<out PsiElement>?): CrModuleStub {
        return CrModuleStubImpl(parentStub, this, psi.visibility)
    }

    override fun serialize(stub: CrModuleStub, dataStream: StubOutputStream) {
        dataStream.writeVisibility(stub.visibility)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrModuleStub {
        val visibility = dataStream.readVisibility()
        return CrModuleStubImpl(parentStub, this, visibility)
    }

    override fun indexStub(stub: CrModuleStub, sink: IndexSink) = indexType(stub, sink)
}