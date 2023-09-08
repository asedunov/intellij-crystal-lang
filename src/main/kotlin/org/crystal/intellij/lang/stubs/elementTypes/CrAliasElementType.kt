package org.crystal.intellij.lang.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.crystal.intellij.lang.psi.CrAlias
import org.crystal.intellij.lang.stubs.api.CrAliasStub
import org.crystal.intellij.lang.stubs.impl.CrAliasStubImpl
import org.crystal.intellij.lang.stubs.indexes.indexType

object CrAliasElementType : CrStubElementType<CrAlias, CrAliasStub>(
    "CR_ALIAS_DEFINITION",
    ::CrAlias,
    ::CrAlias
) {
    override fun createStub(psi: CrAlias, parentStub: StubElement<out PsiElement>?): CrAliasStub {
        return CrAliasStubImpl(parentStub, psi.visibility)
    }

    override fun serialize(stub: CrAliasStub, dataStream: StubOutputStream) {
        dataStream.writeVisibility(stub.visibility)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrAliasStub {
        val visibility = dataStream.readVisibility()
        return CrAliasStubImpl(parentStub, visibility)
    }

    override fun indexStub(stub: CrAliasStub, sink: IndexSink) = indexType(stub, sink)
}