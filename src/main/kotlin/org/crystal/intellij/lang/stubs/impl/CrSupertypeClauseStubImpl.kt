package org.crystal.intellij.lang.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.lang.psi.CrSupertypeClause
import org.crystal.intellij.lang.stubs.api.CrSupertypeClauseStub
import org.crystal.intellij.lang.stubs.elementTypes.CrSupertypeClauseElementType

class CrSupertypeClauseStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrSupertypeClause>(parent, CrSupertypeClauseElementType), CrSupertypeClauseStub