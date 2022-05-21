package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_METHOD_DEFINITION
import org.crystal.intellij.resolve.cache.newResolveSlice
import org.crystal.intellij.resolve.resolveFacade
import org.crystal.intellij.resolve.symbols.CrMethodSym
import org.crystal.intellij.resolve.symbols.CrModuleLikeSym
import org.crystal.intellij.stubs.api.CrMethodStub

class CrMethod : CrDefinitionWithFqNameImpl<CrMethod, CrMethodStub>,
    CrFunctionLikeDefinition,
    CrSimpleNameElementHolder,
    CrTypeParameterHolder
{
    companion object {
        private val SYMBOL = newResolveSlice<CrMethod, CrMethodSym>("SYMBOL")
    }

    constructor(stub: CrMethodStub) : super(stub, CR_METHOD_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitMethod(this)

    val receiver: CrMethodReceiver?
        get() = firstChild?.skipWhitespacesAndCommentsForward() as? CrMethodReceiver

    fun resolveSymbol(): CrMethodSym? {
        val facade = project.resolveFacade
        return facade.resolveCache.getOrCompute(SYMBOL, this) {
            val name = name ?: ""
            val typeDef = parentStubOrPsiOfType<CrModuleLikeDefinition<*, *>>()
            val namespace = if (typeDef != null) {
                typeDef.resolveSymbol() as? CrModuleLikeSym ?: return@getOrCompute null
            } else facade.program
            CrMethodSym(name, namespace, this)
        }
    }
}