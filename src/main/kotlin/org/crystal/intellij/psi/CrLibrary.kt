package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_LIBRARY_DEFINITION
import org.crystal.intellij.stubs.api.CrLibraryStub

class CrLibrary : CrDefinitionWithFqNameImpl<CrLibrary, CrLibraryStub>, CrBodyHolder, CrSimpleNameElementHolder {
    constructor(stub: CrLibraryStub) : super(stub, CR_LIBRARY_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitLibrary(this)
}