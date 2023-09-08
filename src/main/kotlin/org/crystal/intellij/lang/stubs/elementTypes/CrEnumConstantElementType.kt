package org.crystal.intellij.lang.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import org.crystal.intellij.lang.psi.CrEnumConstant
import org.crystal.intellij.lang.stubs.api.CrEnumConstantStub
import org.crystal.intellij.lang.stubs.impl.CrEnumConstantStubImpl
import org.crystal.intellij.lang.stubs.indexes.indexVariable

object CrEnumConstantElementType : CrStubElementType<CrEnumConstant, CrEnumConstantStub>(
    "CR_ENUM_CONSTANT_DEFINITION",
    ::CrEnumConstant,
    ::CrEnumConstant
) {
    override fun createStub(psi: CrEnumConstant, parentStub: StubElement<out PsiElement>?): CrEnumConstantStub {
        return CrEnumConstantStubImpl(parentStub)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrEnumConstantStub {
        return CrEnumConstantStubImpl(parentStub)
    }

    override fun indexStub(stub: CrEnumConstantStub, sink: IndexSink) {
        indexVariable(stub, sink)
    }
}