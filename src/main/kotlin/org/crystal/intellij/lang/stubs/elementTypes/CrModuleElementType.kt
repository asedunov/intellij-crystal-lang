package org.crystal.intellij.lang.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.crystal.intellij.lang.psi.CrModule
import org.crystal.intellij.lang.stubs.api.CrModuleStub
import org.crystal.intellij.lang.stubs.indexes.indexSuperclass
import org.crystal.intellij.lang.stubs.indexes.indexType
import org.crystal.intellij.lang.stubs.impl.CrModuleStubImpl

object CrModuleElementType : CrStubElementType<CrModule, CrModuleStub>(
    "CR_MODULE_DEFINITION",
    ::CrModule,
    ::CrModule
) {
    override fun createStub(psi: CrModule, parentStub: StubElement<out PsiElement>?): CrModuleStub {
        return CrModuleStubImpl(parentStub, psi.visibility)
    }

    override fun serialize(stub: CrModuleStub, dataStream: StubOutputStream) {
        dataStream.writeVisibility(stub.visibility)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrModuleStub {
        val visibility = dataStream.readVisibility()
        return CrModuleStubImpl(parentStub, visibility)
    }

    override fun indexStub(stub: CrModuleStub, sink: IndexSink) {
        indexType(stub, sink)
        indexSuperclass(stub, sink)
    }
}