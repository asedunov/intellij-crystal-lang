package org.crystal.intellij.lang.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import org.crystal.intellij.lang.psi.CrAnnotation
import org.crystal.intellij.lang.stubs.api.CrAnnotationStub
import org.crystal.intellij.lang.stubs.impl.CrAnnotationStubImpl
import org.crystal.intellij.lang.stubs.indexes.indexType

object CrAnnotationElementType : CrStubElementType<CrAnnotation, CrAnnotationStub>(
    "CR_ANNOTATION_DEFINITION",
    ::CrAnnotation,
    ::CrAnnotation
) {
    override fun createStub(psi: CrAnnotation, parentStub: StubElement<out PsiElement>?): CrAnnotationStub {
        return CrAnnotationStubImpl(parentStub)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrAnnotationStub {
        return CrAnnotationStubImpl(parentStub)
    }

    override fun indexStub(stub: CrAnnotationStub, sink: IndexSink) = indexType(stub, sink)
}