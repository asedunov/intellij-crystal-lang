package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_LIBRARY_DEFINITION
import org.crystal.intellij.lang.stubs.api.CrLibraryStub

class CrLibrary : CrModuleLikeDefinition<CrLibrary, CrLibraryStub> {
    constructor(stub: CrLibraryStub) : super(stub, CR_LIBRARY_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitLibrary(this)
}