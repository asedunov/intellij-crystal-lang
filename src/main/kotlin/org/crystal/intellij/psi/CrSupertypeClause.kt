package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_SUPERTYPE_CLAUSE
import org.crystal.intellij.stubs.api.CrSupertypeClauseStub

class CrSupertypeClause : CrStubbedElementImpl<CrSupertypeClauseStub> {
    constructor(stub: CrSupertypeClauseStub) : super(stub, CR_SUPERTYPE_CLAUSE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitSupertypeClause(this)
}