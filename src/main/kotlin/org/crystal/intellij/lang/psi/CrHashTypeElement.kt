package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.lang.parser.CR_HASH_TYPE
import org.crystal.intellij.lang.stubs.api.CrTypeStub

class CrHashTypeElement : CrTypeElement<CrHashTypeElement> {
    constructor(stub: CrTypeStub<CrHashTypeElement>) : super(stub, CR_HASH_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitHashType(this)

    val leftType: CrTypeElement<*>?
        get() {
            greenStub?.let { return it.childrenStubs.getOrNull(0)?.psi as? CrTypeElement<*> }
            return firstChild as? CrTypeElement<*>
        }

    val rightType: CrTypeElement<*>?
        get() {
            greenStub?.let { return it.childrenStubs.getOrNull(1)?.psi as? CrTypeElement<*> }
            return leftType?.nextSiblingOfType()
        }
}