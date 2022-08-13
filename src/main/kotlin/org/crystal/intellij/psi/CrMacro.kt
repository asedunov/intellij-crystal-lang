package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import org.crystal.intellij.parser.CR_MACRO_DEFINITION
import org.crystal.intellij.resolve.cache.newResolveSlice
import org.crystal.intellij.resolve.cache.resolveCache
import org.crystal.intellij.resolve.resolveFacade
import org.crystal.intellij.resolve.symbols.CrMacroSignature
import org.crystal.intellij.resolve.symbols.CrMacroSym
import org.crystal.intellij.resolve.symbols.CrModuleLikeSym
import org.crystal.intellij.stubs.api.CrMacroStub

class CrMacro : CrDefinitionWithFqNameImpl<CrMacro, CrMacroStub>,
    CrSimpleNameElementHolder,
    CrFunctionLikeDefinition,
    CrSymbolOrdinalHolder {
    companion object {
        private val SIGNATURE = newResolveSlice<CrMacro, CrMacroSignature>("SIGNATURE")
        private val SYMBOL = newResolveSlice<CrMacro, CrMacroSym>("SYMBOL")
    }

    constructor(stub: CrMacroStub) : super(stub, CR_MACRO_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitMacro(this)

    private val splatIndex: Int
        get() = parameters.indexOfFirst { it.kind == CrParameterKind.SPLAT }

    private val requiredNamedParameters: List<CrSimpleParameter>
        get() {
            val splatIndex = splatIndex
            val parameters = parameters.toList()
            if (splatIndex < 0 || splatIndex == parameters.lastIndex) return emptyList()
            return parameters.subList(splatIndex + 1, parameters.size).filter { !it.hasInitializer }
        }

    val signature: CrMacroSignature
        get() = project.resolveCache.getOrCompute(SIGNATURE, this) {
            val parameters = parameters.toList()
            val paramCount = parameters.size
            val hasDoubleSplat = parameters.any { it.kind == CrParameterKind.DOUBLE_SPLAT }
            val externalNames = requiredNamedParameters.mapNotNull { it.externalName }
            CrMacroSignature(name ?: "", paramCount, splatIndex, hasDoubleSplat, externalNames).intern()
        } ?: CrMacroSignature(name ?: "")

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