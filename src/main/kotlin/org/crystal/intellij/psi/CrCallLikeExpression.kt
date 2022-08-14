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
import org.crystal.intellij.resolve.scopes.getTypeAs
import org.crystal.intellij.resolve.symbols.*

sealed class CrCallLikeExpression(node: ASTNode) : CrExpressionImpl(node), CrSimpleNameElementHolder {
    companion object {
        private val CALL = newResolveSlice<CrCallLikeExpression, CrCall>("CALL")
        private val CALLEE_TARGET = newResolveSlice<CrCallLikeExpression, CrSym<*>>("CALLEE_TARGET")
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

    fun resolveCallee(): CrSym<*>? = project.resolveCache.getOrCompute(CALLEE_TARGET, this) {
        tryMacroResolve()
    }

    private fun tryMacroResolve(): CrMacroSym? {
        val nameKind = nameElement?.kind ?: return null
        if (nameKind == CrNameKind.SUPER || nameKind == CrNameKind.PREVIOUS_DEF) return null

        val program = project.resolveFacade.program

        val scopeSymbol = when(val receiver = receiver) {
            null -> if (isGlobal) project.resolveFacade.program else currentModuleLikeSym()?.metaclass
            is CrPathExpression -> (receiver.nameElement?.resolveSymbol() as? CrModuleLikeSym)?.metaclass
            else -> null
        } ?: return null

        // TODO: "with" scope
        var macro = scopeSymbol.lookupMacro()
        if (macro == null && scopeSymbol.instanceSym is CrModuleSym) {
            macro = program.memberScope.getTypeAs<CrModuleLikeSym>(CrStdFqNames.OBJECT)?.lookupMacro()
        }
        if (macro == null) {
            macro = program.lookupMacro()
        }
        // TODO: File modules
        return macro
    }

    private fun CrModuleLikeSym.lookupMacro(): CrMacroSym? {
        val call = call ?: return null
        return memberScope.getMacro(call)
    }
}