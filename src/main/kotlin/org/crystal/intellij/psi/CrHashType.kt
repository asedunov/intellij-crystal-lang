package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_HASH_TYPE
import org.crystal.intellij.stubs.api.CrTypeStub

class CrHashType : CrType {
    constructor(stub: CrTypeStub) : super(stub, CR_HASH_TYPE)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitHashType(this)

    val leftType: CrType?
        get() {
            greenStub?.let { return it.childrenStubs.getOrNull(0)?.psi as? CrType }
            return firstChild as? CrType
        }

    val rightType: CrType?
        get() {
            greenStub?.let { return it.childrenStubs.getOrNull(1)?.psi as? CrType }
            return leftType?.nextSiblingOfType()
        }
}