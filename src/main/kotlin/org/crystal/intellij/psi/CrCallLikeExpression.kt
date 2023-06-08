package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.util.elementType
import org.crystal.intellij.lexer.CR_PATH_OP
import org.crystal.intellij.resolve.CrCall
import org.crystal.intellij.resolve.CrStdFqNames
import org.crystal.intellij.resolve.cache.newResolveSlice
import org.crystal.intellij.resolve.cache.resolveCache
import org.crystal.intellij.resolve.currentModuleLikeSym
import org.crystal.intellij.resolve.resolveFacade
import org.crystal.intellij.resolve.scopes.CrResolvedMacroCall
import org.crystal.intellij.resolve.scopes.getTypeAs
import org.crystal.intellij.resolve.symbols.CrMacroName
import org.crystal.intellij.resolve.symbols.CrModuleLikeSym
import org.crystal.intellij.resolve.symbols.CrModuleSym
import org.crystal.intellij.resolve.symbols.instanceSym

sealed class CrCallLikeExpression(node: ASTNode) : CrExpressionImpl(node), CrSimpleNameElementHolder {
    companion object {
        private val CALL = newResolveSlice<CrCallLikeExpression, CrCall>("CALL")
        private val RESOLVED_CALL = newResolveSlice<CrCallLikeExpression, CrResolvedMacroCall>("RESOLVED_CALL")
        private val CANDIDATE_RESOLVED_CALLS = newResolveSlice<CrCallLikeExpression, List<CrResolvedMacroCall>>("CANDIDATE_RESOLVED_CALLS")
    }

    private val isGlobal: Boolean
        get() = firstChild.elementType == CR_PATH_OP

    abstract val receiver: CrExpression?

    abstract val argumentList: CrArgumentList?

    abstract val blockArgument: CrBlockExpression?

    private val call: CrCall?
        get() = project.resolveCache.getOrCompute(CALL, this) {
            CrCall(this)
        }

    fun resolveCall(): CrResolvedMacroCall? = project.resolveCache.getOrCompute(RESOLVED_CALL, this) {
        val call = call ?: return@getOrCompute null
        macroModulesToTry().firstNotNullOfOrNull { it.memberScope.lookupMacroCall(call) }
    }

    fun resolveCandidateCalls(): List<CrResolvedMacroCall> {
        val resolvedCall = resolveCall()
        if (resolvedCall != null) return listOf(resolvedCall)

        val name = name ?: return emptyList()
        val call = call ?: return emptyList()

        return project.resolveCache.getOrCompute(CANDIDATE_RESOLVED_CALLS, this) {
            macroModulesToTry()
                .flatMap { it.memberScope.getAllMacros(CrMacroName(name)).asSequence() }
                .distinctBy { it.signature }
                .map { CrResolvedMacroCall(call, it) }
                .toList()
        } ?: emptyList()
    }

    private fun tryMacroResolve(): CrResolvedMacroCall? {
        val nameKind = nameElement?.kind ?: return null
        if (nameKind == CrNameKind.SUPER || nameKind == CrNameKind.PREVIOUS_DEF) return null

        val program = project.resolveFacade.program

        val scopeSymbol = when(val receiver = receiver) {
            null -> if (isGlobal) project.resolveFacade.program else currentModuleLikeSym()?.metaclass
            is CrPathExpression -> (receiver.nameElement?.resolveSymbol() as? CrModuleLikeSym)?.metaclass
            else -> null
        } ?: return null

        // TODO: "with" scope
        var macro = scopeSymbol.lookupMacroCall()
        if (macro == null && scopeSymbol.instanceSym is CrModuleSym) {
            macro = program.memberScope.getTypeAs<CrModuleLikeSym>(CrStdFqNames.OBJECT)?.lookupMacroCall()
        }
        if (macro == null) {
            macro = program.lookupMacroCall()
        }
        // TODO: File modules
        return macro
    }

    private fun CrModuleLikeSym.lookupMacroCall(): CrResolvedMacroCall? {
        val call = call ?: return null
        return memberScope.lookupMacroCall(call)
    }
}