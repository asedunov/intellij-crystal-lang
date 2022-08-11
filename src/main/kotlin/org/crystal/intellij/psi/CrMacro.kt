package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_MACRO_DEFINITION
import org.crystal.intellij.resolve.cache.newResolveSlice
import org.crystal.intellij.resolve.resolveFacade
import org.crystal.intellij.resolve.symbols.CrMacroSym
import org.crystal.intellij.resolve.symbols.CrModuleLikeSym
import org.crystal.intellij.stubs.api.CrMacroStub

class CrMacro : CrDefinitionWithFqNameImpl<CrMacro, CrMacroStub>,
    CrSimpleNameElementHolder,
    CrFunctionLikeDefinition,
    CrSymbolOrdinalHolder {
    companion object {
        private val SYMBOL = newResolveSlice<CrMacro, CrMacroSym>("SYMBOL")
    }

    constructor(stub: CrMacroStub) : super(stub, CR_MACRO_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitMacro(this)

    fun resolveSymbol(): CrMacroSym? {
        val facade = project.resolveFacade
        return facade.resolveCache.getOrCompute(SYMBOL, this) {
            val name = name ?: ""
            val typeDef = parentStubOrPsiOfType<CrModuleLikeDefinition<*, *>>()
            val namespace = if (typeDef != null) {
                typeDef.resolveSymbol() as? CrModuleLikeSym ?: return@getOrCompute null
            } else facade.program
            CrMacroSym(name, namespace.metaclass, this)
        }
    }
}