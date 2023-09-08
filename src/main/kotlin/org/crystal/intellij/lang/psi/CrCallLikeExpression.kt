package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.crystal.intellij.lang.lexer.CR_PATH_OP
import org.crystal.intellij.lang.resolve.CrCall
import org.crystal.intellij.lang.resolve.CrStdFqNames
import org.crystal.intellij.lang.resolve.cache.newResolveSlice
import org.crystal.intellij.lang.resolve.cache.resolveCache
import org.crystal.intellij.lang.resolve.currentModuleLikeSym
import org.crystal.intellij.lang.resolve.resolveFacade
import org.crystal.intellij.lang.resolve.scopes.CrResolvedMacroCall
import org.crystal.intellij.lang.resolve.scopes.getTypeAs
import org.crystal.intellij.lang.resolve.symbols.CrMacroName
import org.crystal.intellij.lang.resolve.symbols.CrModuleLikeSym
import org.crystal.intellij.lang.resolve.symbols.CrModuleSym
import org.crystal.intellij.lang.resolve.symbols.instanceSym

sealed class CrCallLikeExpression(
    node: ASTNode
) : CrExpressionImpl(node), CrSimpleNameElementHolder, CrExpressionWithReceiver {
    companion object {
        private val CALL = newResolveSlice<CrCallLikeExpression, CrCall>("CALL")
        private val RESOLVED_CALL = newResolveSlice<CrCallLikeExpression, CrResolvedMacroCall>("RESOLVED_CALL")
        private val CANDIDATE_RESOLVED_CALLS = newResolveSlice<CrCallLikeExpression, List<CrResolvedMacroCall>>("CANDIDATE_RESOLVED_CALLS")
    }

    val isGlobal: Boolean
        get() = firstChild.elementType == CR_PATH_OP

    abstract val argumentList: CrArgumentList?

    abstract val blockArgument: CrBlockExpression?

    override fun getParent(): PsiElement = explicitParent ?: super.getParent()

    val call: CrCall?
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

        return project.resolveCache.getOrCompute(CANDIDATE_RESOLVED_CALLS, this) {
            val name = name ?: return@getOrCompute null
            val call = call ?: return@getOrCompute null
            macroModulesToTry()
                .flatMap { it.memberScope.getAllMacros(CrMacroName(name)).asSequence() }
                .distinctBy { it.signature }
                .map { CrResolvedMacroCall(call, it) }
                .toList()
        } ?: emptyList()
    }

    fun getCompletionCandidates() = macroModulesToTry().flatMap { module ->
        module.memberScope.getAllMacros().filter { it.isDefined && it.isVisible(parent) }
    }

    private fun macroModulesToTry(): Sequence<CrModuleLikeSym> = sequence {
        val nameKind = nameElement?.kind ?: return@sequence
        if (nameKind == CrNameKind.SUPER || nameKind == CrNameKind.PREVIOUS_DEF) return@sequence

        val program = project.resolveFacade.program

        val scopeSymbol = when(val receiver = receiver) {
            null -> if (isGlobal) project.resolveFacade.program else currentModuleLikeSym()?.metaclass
            is CrPathExpression -> (receiver.nameElement?.resolveSymbol() as? CrModuleLikeSym)?.metaclass
            else -> null
        } ?: return@sequence

        // TODO: "with" scope
        yield(scopeSymbol)
        if (scopeSymbol.instanceSym is CrModuleSym) {
            val objectSym = program.memberScope.getTypeAs<CrModuleLikeSym>(CrStdFqNames.OBJECT)
            if (objectSym != null) yield(objectSym)
        }
        if (scopeSymbol != program) {
            yield(program)
        }

        // TODO: File modules
    }
}