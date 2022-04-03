package org.crystal.intellij.stubs.elementTypes

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import org.crystal.intellij.psi.CrRequireExpression
import org.crystal.intellij.stubs.api.CrRequireStub
import org.crystal.intellij.stubs.impl.CrRequireStubImpl

object CrRequireElementType : CrStubElementType<CrRequireExpression, CrRequireStub>(
    "CR_REQUIRE_EXPRESSION",
    ::CrRequireExpression,
    ::CrRequireExpression
) {
    override fun shouldCreateStub(node: ASTNode) = true

    override fun createStub(psi: CrRequireExpression, parentStub: StubElement<out PsiElement>?): CrRequireStub {
        return CrRequireStubImpl(parentStub, psi.path?.stringValue)
    }

    override fun serialize(stub: CrRequireStub, dataStream: StubOutputStream) {
        dataStream.writeName(stub.filePath)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrRequireStub {
        val filePath = dataStream.readNameString()
        return CrRequireStubImpl(parentStub, filePath)
    }
}