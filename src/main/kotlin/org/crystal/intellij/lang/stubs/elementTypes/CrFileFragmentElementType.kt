package org.crystal.intellij.lang.stubs.elementTypes

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import org.crystal.intellij.lang.psi.CrFileFragment
import org.crystal.intellij.lang.stubs.api.CrFileFragmentStub
import org.crystal.intellij.lang.stubs.impl.CrFileFragmentStubImpl

object CrFileFragmentElementType : CrStubElementType<CrFileFragment, CrFileFragmentStub>(
    "CR_FILE_FRAGMENT",
    ::CrFileFragment,
    ::CrFileFragment
) {
    override fun shouldCreateStub(node: ASTNode) = true

    override fun createStub(psi: CrFileFragment, parentStub: StubElement<out PsiElement>?): CrFileFragmentStub {
        return CrFileFragmentStubImpl(parentStub)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrFileFragmentStub {
        return CrFileFragmentStubImpl(parentStub)
    }
}