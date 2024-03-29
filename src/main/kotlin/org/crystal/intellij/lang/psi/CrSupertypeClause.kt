package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_SUPERTYPE_CLAUSE
import org.crystal.intellij.lang.resolve.symbols.CrProperTypeSym
import org.crystal.intellij.lang.stubs.api.CrSupertypeClauseStub

class CrSupertypeClause : CrStubbedElementImpl<CrSupertypeClauseStub>, CrSymbolOrdinalHolder {
    constructor(stub: CrSupertypeClauseStub) : super(stub, CR_SUPERTYPE_CLAUSE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitSupertypeClause(this)

    val type: CrTypeElement<*>?
        get() = stubChildOfType()

    fun resolveSymbol() = type?.typePath?.resolveSymbol() as? CrProperTypeSym

    override fun ordinal() = (parent as? CrTypeDefinition)?.ordinal()
}