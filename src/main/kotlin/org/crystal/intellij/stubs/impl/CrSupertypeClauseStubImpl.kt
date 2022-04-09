package org.crystal.intellij.stubs.impl

import com.intellij.psi.stubs.StubElement
import org.crystal.intellij.psi.CrSupertypeClause
import org.crystal.intellij.stubs.api.CrSupertypeClauseStub
import org.crystal.intellij.stubs.elementTypes.CrSupertypeClauseElementType

class CrSupertypeClauseStubImpl(
    parent: StubElement<*>?
) : CrStubElementImpl<CrSupertypeClause>(parent, CrSupertypeClauseElementType), CrSupertypeClauseStub