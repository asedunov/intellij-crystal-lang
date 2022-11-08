package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_CONSTANT_DEFINITION
import org.crystal.intellij.resolve.FqName
import org.crystal.intellij.resolve.symbols.CrConstantLikeSym
import org.crystal.intellij.stubs.api.CrConstantStub

class CrConstant : CrDefinitionWithFqNameImpl<CrConstant, CrConstantStub>,
    CrDefinitionWithInitializer,
    CrPathNameElementHolder,
    CrConstantSource
{
    constructor(stub: CrConstantStub) : super(stub, CR_CONSTANT_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitConstant(this)

    override val fqName: FqName?
        get() = super.fqName

    override fun resolveSymbol(): CrConstantLikeSym<*>? = nameElement?.resolveSymbol()
}