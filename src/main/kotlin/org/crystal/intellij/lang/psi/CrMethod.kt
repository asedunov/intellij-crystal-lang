package org.crystal.intellij.lang.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.SyntaxTraverser
import com.intellij.psi.util.descendantsOfType
import com.intellij.util.containers.JBIterable
import org.crystal.intellij.lang.parser.CR_METHOD_DEFINITION
import org.crystal.intellij.lang.resolve.cache.newResolveSlice
import org.crystal.intellij.lang.resolve.cache.resolveCache
import org.crystal.intellij.lang.resolve.resolveFacade
import org.crystal.intellij.lang.resolve.symbols.CrMethodSym
import org.crystal.intellij.lang.resolve.symbols.CrModuleLikeSym
import org.crystal.intellij.lang.stubs.api.CrMethodStub
import kotlin.math.max

class CrMethod : CrDefinitionWithFqNameImpl<CrMethod, CrMethodStub>,
    CrFunctionLikeDefinition,
    CrSimpleNameElementHolder,
    CrTypeParameterHolder
{
    companion object {
        private val SYMBOL = newResolveSlice<CrMethod, CrMethodSym>("SYMBOL")
        private val BLOCK_ARITY = newResolveSlice<CrMethod, Int>("BLOCK_ARITY")
        private val IS_MACRO_DEF = newResolveSlice<CrMethod, Boolean>("IS_MACRO_DEF")
    }

    constructor(stub: CrMethodStub) : super(stub, CR_METHOD_DEFINITION)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitMethod(this)

    val receiver: CrMethodReceiver?
        get() = firstChild?.skipWhitespacesAndCommentsForward() as? CrMethodReceiver

    override val returnType: CrTypeElement<*>?
        get() {
            val receiver = receiver
            return if (receiver != null) receiver.nextSiblingOfType() else super.returnType
        }

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

    val body: CrBlockExpression?
        get() = childOfType()

    val blockArity: Int
        get() = project.resolveCache.getOrCompute(BLOCK_ARITY, this) {
            val blockParam = parameters.firstOrNull { it.kind == CrParameterKind.BLOCK }
            if (blockParam != null) {
                val inputs = (blockParam.type as? CrProcTypeElement)?.inputList?.elements ?: JBIterable.empty()
                return@getOrCompute inputs.size()
            }
            var arity = -1
            accept(
                object : CrRecursiveVisitor() {
                    override fun visitYieldExpression(o: CrYieldExpression) {
                        if (o.subject != null) {
                            arity = max(arity, 1)
                        }
                        val args = o.argumentList?.elements ?: JBIterable.empty()
                        arity = max(arity, args.size())

                        super.visitYieldExpression(o)
                    }
                }
            )
            arity
        } ?: -1

    private val PsiElement.isMacroExpression: Boolean
        get() = this is CrMacroWrapperStatement ||
                this is CrMacroExpression ||
                this == (parent as? CrMacroIfStatement)?.condition ||
                this == (parent as? CrMacroForStatement)?.iterable ||
                parent is CrMacroVariableExpression

    val isMacroDef: Boolean
        get() = project.resolveCache.getOrCompute(IS_MACRO_DEF, this) {
            val macroExpressions = SyntaxTraverser
                .psiTraverser(body)
                .expandAndSkip { !it.isMacroExpression }
                .filter(CrExpression::class.java)
            for (macroExpression in macroExpressions) {
                for (reference in macroExpression.descendantsOfType<CrReferenceExpression>()) {
                    if (reference.reference == null &&
                        reference.nameElement?.kind == CrNameKind.INSTANCE_VARIABLE &&
                        reference.name == "type") return@getOrCompute true
                }
            }
            false
        } ?: false
}