package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_FILE_FRAGMENT
import org.crystal.intellij.stubs.api.CrFileFragmentStub

class CrFileFragment : CrStubbedElementImpl<CrFileFragmentStub>, CrTopLevelHolder {
    constructor(stub: CrFileFragmentStub) : super(stub, CR_FILE_FRAGMENT)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitFileFragment(this)
}