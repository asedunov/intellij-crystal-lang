package org.crystal.intellij.lang.stubs.elementTypes

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import org.crystal.intellij.lang.psi.CrSupertypeClause
import org.crystal.intellij.lang.stubs.api.CrSupertypeClauseStub
import org.crystal.intellij.lang.stubs.impl.CrSupertypeClauseStubImpl

object CrSupertypeClauseElementType : CrStubElementType<CrSupertypeClause, CrSupertypeClauseStub>(
    "CR_SUPERTYPE_CLAUSE",
    ::CrSupertypeClause,
    ::CrSupertypeClause
) {
    override fun createStub(psi: CrSupertypeClause, parentStub: StubElement<out PsiElement>?): CrSupertypeClauseStub {
        return CrSupertypeClauseStubImpl(parentStub)
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): CrSupertypeClauseStub {
        return CrSupertypeClauseStubImpl(parentStub)
    }
}