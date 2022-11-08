package org.crystal.intellij.psi

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.util.isAncestor
import org.crystal.intellij.parser.CR_PATH_NAME_ELEMENT
import org.crystal.intellij.references.CrPathReference
import org.crystal.intellij.resolve.FqName
import org.crystal.intellij.resolve.cache.newResolveSlice
import org.crystal.intellij.resolve.cache.resolveCache
import org.crystal.intellij.resolve.resolveFacade
import org.crystal.intellij.resolve.scopes.CrScope
import org.crystal.intellij.resolve.symbols.*
import org.crystal.intellij.stubs.api.CrPathStub

@Suppress("UnstableApiUsage")
open class CrPathNameElement : CrStubbedElementImpl<CrPathStub>, CrNameElement, CrConstantSource {
    companion object {
        private val PATH_TARGET = newResolveSlice<CrPathNameElement, CrConstantLikeSym<*>>("PATH_TARGET")
        private val PATH_RESOLVE_SCOPE = newResolveSlice<CrPathNameElement, CrScope>("PATH_RESOLVE_SCOPE")

        val EXPLICIT_PARENT = Key.create<PsiElement>("EXPLICIT_PARENT")
        val EXPLICIT_QUALIFIER = Key.create<CrPathNameElement>("EXPLICIT_QUALIFIER")
    }

    constructor(stub: CrPathStub) : super(stub, CR_PATH_NAME_ELEMENT)

    constructor(node: ASTNode) : super(node)

    override fun accept(visitor: CrVisitor) = visitor.visitPathNameElement(this)

    override fun getParent(): PsiElement {
        return getUserData(EXPLICIT_PARENT) ?: super.getParent()
    }

    override val kind: CrNameKind
        get() = CrNameKind.PATH

    val isGlobal: Boolean
        get() = name.isEmpty()

    val qualifier: CrPathNameElement?
        get() = getUserData(EXPLICIT_QUALIFIER) ?: stubChildOfType()

    val item: CrConstantName?
        get() = childOfType()

    override fun getName() = greenStub?.name ?: item?.name ?: ""

    override fun setName(name: String): CrNameElement {
        return replaceTyped(CrPsiFactory.getInstance(project).createPathNameElement(name))
    }

    override val sourceName: String
        get() = name

    override val fqName: FqName?
        get() = when (val p = parent) {
            is CrPathNameElement -> p.fqName?.parent
            is CrTypeDefinition -> p.fqName
            else -> null
        }

    override fun ordinal(): CrSymbolOrdinal? {
        var p = parent
        if (p is CrPathTypeElement) p = p.parent
        if (p is CrInstantiatedTypeElement) p = p.parent
        return (p as? CrSymbolOrdinalHolder)?.ordinal()
    }

    private val pathResolveScope: CrScope?
        get() = project.resolveCache.getOrCompute(PATH_RESOLVE_SCOPE, this) {
            val qualifier = qualifier
            if (qualifier != null) return@getOrCompute (qualifier.resolveSymbol() as? CrModuleLikeSym)?.memberScope

            val include = parentStubOrPsiOfType<CrIncludeLikeExpression>()
            if (include != null) return@getOrCompute include.pathResolveScope

            val containingMethod = parentStubOrPsiOfType<CrMethod>()
            if (containingMethod != null) {
                return@getOrCompute containingMethod.resolveSymbol()?.containedScope
            }

            var containingType = parentStubOrPsiOfType<CrModuleLikeDefinition<*, *>>()
            if (containingType?.nameElement?.isAncestor(this, false) == true ||
                (containingType as? CrSuperTypeAware)?.superTypeClause?.isAncestor(this, false) == true
            ) {
                containingType = containingType.parentStubOrPsiOfType()
            }
            val scopeOwner =
                if (containingType != null) containingType.resolveSymbol() else project.resolveFacade.program
            (scopeOwner as? CrModuleLikeSym)?.memberScope
        }

    override fun resolveSymbol(): CrConstantLikeSym<*>? {
        if (isGlobal) return project.resolveFacade.program
        return project.resolveCache.getOrCompute(PATH_TARGET, this) {
            val targetSym = pathResolveScope?.getConstant(name, qualifier == null)
            if (targetSym is CrConstantLikeSym<*> && checkCandidate(targetSym)) targetSym else null
        }
    }

    fun getCompletionCandidates(): Sequence<CrConstantLikeSym<*>> {
        val scope = pathResolveScope ?: return emptySequence()
        return scope
            .getAllConstants(qualifier == null)
            .filter(::checkCandidate)
    }

    private fun checkCandidate(type: CrConstantLikeSym<*>): Boolean {
        if (parentStubOrPsiOfType<CrSupertypeClause>() != null
            || parentStubOrPsiOfType<CrIncludeLikeExpression>() != null) {
            val targetOrdinal = type.ordinal ?: return false
            val currentOrdinal =
                parentStubOrPsiOfType<CrSymbolOrdinalHolder>()?.ordinal() ?: return false
            if (targetOrdinal >= currentOrdinal) return false
        }
        return true
    }

    val ownReference: CrPathReference
        get() = CrPathReference(this)

    override fun getOwnReferences() = listOf(ownReference)
}