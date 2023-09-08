package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.lang.parser.CR_FILE_FRAGMENT
import org.crystal.intellij.lang.stubs.api.CrFileFragmentStub

class CrFileFragment : CrStubbedElementImpl<CrFileFragmentStub>, CrTopLevelHolder, CrFileElement {
    constructor(stub: CrFileFragmentStub) : super(stub, CR_FILE_FRAGMENT)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitFileFragment(this)

    val expressions: JBIterable<CrExpression>
        get() = childrenOfType()
}